import { ElNotification } from 'element-plus'

class NotificationWebSocket {
  constructor() {
    this.ws = null
    this.reconnectCount = 0
    this.maxReconnectCount = 5
    this.reconnectInterval = 5000
    this.heartbeatInterval = 30000
    this.heartbeatTimer = null
    this.userId = null
    this.callbacks = {
      onMessage: [],
      onUnreadCountUpdate: [],
      onConnect: [],
      onDisconnect: []
    }
  }

  /**
   * 连接WebSocket
   * @param {number} userId 用户ID
   */
  connect(userId) {
    if (!userId) {
      console.error('用户ID不能为空')
      return
    }

    this.userId = userId

    // 如果已连接，先断开
    if (this.ws) {
      this.disconnect()
    }

    try {
      // 确定WebSocket URL
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = window.location.host
      const wsUrl = `${protocol}//${host}/websocket/notification/${userId}`

      console.log('正在连接WebSocket:', wsUrl)

      this.ws = new WebSocket(wsUrl)
      this.setupEventHandlers()
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.handleReconnect()
    }
  }

  /**
   * 设置事件处理器
   */
  setupEventHandlers() {
    this.ws.onopen = (event) => {
      console.log('WebSocket连接成功')
      this.reconnectCount = 0
      this.startHeartbeat()
      
      // 触发连接回调
      this.callbacks.onConnect.forEach(callback => {
        try {
          callback(event)
        } catch (error) {
          console.error('连接回调执行失败:', error)
        }
      })
    }

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        console.log('收到WebSocket消息:', data)

        switch (data.type) {
          case 'CONNECT':
            console.log('WebSocket连接确认:', data.message)
            break

          case 'NOTIFICATION':
            this.handleNotification(data)
            break

          case 'UNREAD_COUNT':
            this.handleUnreadCountUpdate(data)
            break

          case 'BROADCAST':
            this.handleBroadcast(data)
            break

          default:
            console.log('未知消息类型:', data.type)
        }

        // 触发消息回调
        this.callbacks.onMessage.forEach(callback => {
          try {
            callback(data)
          } catch (error) {
            console.error('消息回调执行失败:', error)
          }
        })
      } catch (error) {
        console.error('解析WebSocket消息失败:', error)
      }
    }

    this.ws.onclose = (event) => {
      console.log('WebSocket连接关闭:', event.code, event.reason)
      this.stopHeartbeat()
      
      // 触发断开连接回调
      this.callbacks.onDisconnect.forEach(callback => {
        try {
          callback(event)
        } catch (error) {
          console.error('断开连接回调执行失败:', error)
        }
      })

      // 如果不是主动关闭，尝试重连
      if (event.code !== 1000) {
        this.handleReconnect()
      }
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
    }
  }

  /**
   * 处理通知消息
   */
  handleNotification(data) {
    const notification = data.data
    if (notification) {
      // 显示桌面通知
      ElNotification({
        title: notification.title || '新通知',
        message: notification.content || '',
        type: this.getNotificationType(notification.level),
        duration: 5000,
        onClick: () => {
          // 可以在这里处理点击通知的逻辑
          this.handleNotificationClick(notification)
        }
      })
    }
  }

  /**
   * 处理未读数量更新
   */
  handleUnreadCountUpdate(data) {
    const unreadCount = parseInt(data.message) || 0
    console.log('未读数量更新:', unreadCount)
    
    // 触发未读数量更新回调
    this.callbacks.onUnreadCountUpdate.forEach(callback => {
      try {
        callback(unreadCount)
      } catch (error) {
        console.error('未读数量更新回调执行失败:', error)
      }
    })
  }

  /**
   * 处理广播消息
   */
  handleBroadcast(data) {
    ElNotification({
      title: '系统广播',
      message: data.message,
      type: 'info',
      duration: 0 // 不自动关闭
    })
  }

  /**
   * 处理通知点击
   */
  handleNotificationClick(notification) {
    // 这里可以根据通知类型跳转到相应页面
    console.log('点击通知:', notification)
  }

  /**
   * 获取通知类型
   */
  getNotificationType(level) {
    switch (level) {
      case '紧急':
        return 'error'
      case '重要':
        return 'warning'
      default:
        return 'info'
    }
  }

  /**
   * 处理重连
   */
  handleReconnect() {
    if (this.reconnectCount >= this.maxReconnectCount) {
      console.error('WebSocket重连次数超出限制，停止重连')
      return
    }

    this.reconnectCount++
    console.log(`WebSocket重连中... (${this.reconnectCount}/${this.maxReconnectCount})`)

    setTimeout(() => {
      if (this.userId) {
        this.connect(this.userId)
      }
    }, this.reconnectInterval)
  }

  /**
   * 开始心跳
   */
  startHeartbeat() {
    this.stopHeartbeat()
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send('ping')
      }
    }, this.heartbeatInterval)
  }

  /**
   * 停止心跳
   */
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 发送消息
   */
  send(message) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket未连接，无法发送消息')
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    this.stopHeartbeat()
    if (this.ws) {
      this.ws.close(1000, '主动断开连接')
      this.ws = null
    }
  }

  /**
   * 注册回调函数
   */
  on(event, callback) {
    if (this.callbacks[event] && typeof callback === 'function') {
      this.callbacks[event].push(callback)
    }
  }

  /**
   * 移除回调函数
   */
  off(event, callback) {
    if (this.callbacks[event]) {
      const index = this.callbacks[event].indexOf(callback)
      if (index > -1) {
        this.callbacks[event].splice(index, 1)
      }
    }
  }

  /**
   * 获取连接状态
   */
  getReadyState() {
    return this.ws ? this.ws.readyState : WebSocket.CLOSED
  }

  /**
   * 是否已连接
   */
  isConnected() {
    return this.ws && this.ws.readyState === WebSocket.OPEN
  }
}

// 创建全局实例
const notificationWebSocket = new NotificationWebSocket()

export default notificationWebSocket