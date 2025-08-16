<template>
  <el-dropdown 
    trigger="click" 
    @visible-change="onDropdownVisibleChange"
    placement="bottom-end"
    :max-height="400"
  >
    <div class="notification-trigger">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
        <el-button :icon="Bell" circle size="large" />
      </el-badge>
    </div>
    
    <template #dropdown>
      <el-dropdown-menu class="notification-dropdown">
        <!-- 头部 -->
        <div class="dropdown-header">
          <div class="header-left">
            <span class="title">通知消息</span>
            <el-tag v-if="unreadCount > 0" type="primary" size="small">
              {{ unreadCount }}条未读
            </el-tag>
          </div>
          <div class="header-actions">
            <el-button 
              @click="markAllAsRead" 
              :disabled="unreadCount === 0"
              size="small" 
              type="primary" 
              link
            >
              全部已读
            </el-button>
            <el-button @click="goToNotificationPage" size="small" type="primary" link>
              查看全部
            </el-button>
          </div>
        </div>
        
        <!-- 通知列表 -->
        <div class="dropdown-content" v-loading="loading">
          <el-empty v-if="!notifications.length && !loading" description="暂无通知" :image-size="60" />
          
          <div v-else class="notification-items">
            <div
              v-for="notification in notifications"
              :key="notification.id"
              class="notification-item"
              :class="{ 'is-unread': notification.isRead === 0 }"
              @click="handleNotificationClick(notification)"
            >
              <div class="item-indicator" v-if="notification.isRead === 0"></div>
              
              <div class="item-content">
                <div class="item-header">
                  <span class="item-title">{{ notification.title }}</span>
                  <span class="item-time">{{ formatTime(notification.createTime) }}</span>
                </div>
                
                <div class="item-body">
                  <p class="item-text">{{ truncateText(notification.content, 50) }}</p>
                </div>
                
                <div class="item-footer">
                  <el-tag 
                    :type="getNotificationTypeTag(notification.type)"
                    size="small"
                  >
                    {{ getNotificationTypeText(notification.type) }}
                  </el-tag>
                  <span class="item-sender">{{ notification.senderName || '系统' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 底部 -->
        <div class="dropdown-footer" v-if="notifications.length > 0">
          <el-button @click="goToNotificationPage" size="small" style="width: 100%">
            查看全部通知
          </el-button>
        </div>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { 
  getUserNotifications, 
  getUnreadCount, 
  markNotificationsAsRead,
  markAllNotificationsAsRead 
} from '@/api/notification'
import notificationWebSocket from '@/utils/websocket'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)
let pollTimer = null

// 方法
const loadRecentNotifications = async () => {
  loading.value = true
  try {
    const response = await getUserNotifications({
      pageNum: 1,
      pageSize: 5 // 只显示最近5条
    })
    
    if (response.data) {
      notifications.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    const response = await getUnreadCount()
    if (response.data) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读数量失败:', error)
  }
}

const markAllAsRead = async () => {
  try {
    await markAllNotificationsAsRead()
    ElMessage.success('全部标记已读成功')
    await Promise.all([loadRecentNotifications(), loadUnreadCount()])
  } catch (error) {
    ElMessage.error('标记已读失败')
  }
}

const handleNotificationClick = async (notification) => {
  // 如果是未读消息，标记为已读
  if (notification.isRead === 0) {
    try {
      await markNotificationsAsRead([notification.id])
      await Promise.all([loadRecentNotifications(), loadUnreadCount()])
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  
  // 根据通知类型跳转到相应页面
  handleNotificationNavigation(notification)
}

const handleNotificationNavigation = (notification) => {
  const { type, relatedId, relatedType } = notification
  
  try {
    switch (type) {
      case 'TASK_ASSIGN':
      case 'TASK_DEADLINE':
        if (relatedId) {
          router.push({
            path: '/task',
            query: { id: relatedId }
          })
        } else {
          router.push('/task')
        }
        break
        
      case 'WORKLOG_REVIEW':
        if (relatedId) {
          router.push({
            path: '/worklog',
            query: { id: relatedId }
          })
        } else {
          router.push('/worklog')
        }
        break
        
      case 'PROJECT_UPDATE':
        if (relatedId) {
          router.push({
            path: '/project',
            query: { id: relatedId }
          })
        } else {
          router.push('/project')
        }
        break
        
      default:
        // 默认跳转到通知页面
        goToNotificationPage()
        break
    }
  } catch (error) {
    console.error('页面跳转失败:', error)
    ElMessage.error('页面跳转失败')
  }
}

const goToNotificationPage = () => {
  router.push('/notification')
}

const onDropdownVisibleChange = (visible) => {
  if (visible) {
    // 下拉框打开时刷新数据
    Promise.all([loadRecentNotifications(), loadUnreadCount()])
  }
}

const startPolling = () => {
  // 每30秒轮询一次未读数量
  pollTimer = setInterval(() => {
    loadUnreadCount()
  }, 30000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

const getNotificationTypeText = (type) => {
  const typeMap = {
    'TASK_ASSIGN': '任务分配',
    'WORKLOG_REVIEW': '日志审核',
    'TASK_DEADLINE': '任务截止',
    'PROJECT_UPDATE': '项目更新',
    'SYSTEM_MESSAGE': '系统消息'
  }
  return typeMap[type] || type
}

const getNotificationTypeTag = (type) => {
  const tagMap = {
    'TASK_ASSIGN': 'primary',
    'WORKLOG_REVIEW': 'success',
    'TASK_DEADLINE': 'warning',
    'PROJECT_UPDATE': 'info',
    'SYSTEM_MESSAGE': ''
  }
  return tagMap[type] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) { // 小于1分钟
    return '刚刚'
  } else if (diff < 3600000) { // 小于1小时
    return Math.floor(diff / 60000) + '分钟前'
  } else if (diff < 86400000) { // 小于1天
    return Math.floor(diff / 3600000) + '小时前'
  } else {
    return Math.floor(diff / 86400000) + '天前'
  }
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// WebSocket处理函数
const handleUnreadCountUpdate = (count) => {
  unreadCount.value = count
}

const handleNewNotification = () => {
  // 收到新通知时刷新列表
  loadRecentNotifications()
}

// 生命周期
onMounted(() => {
  loadUnreadCount()
  startPolling()
  
  // 连接WebSocket
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || '{}')
  if (loginUser.id) {
    notificationWebSocket.connect(loginUser.id)
    
    // 注册WebSocket回调
    notificationWebSocket.on('onUnreadCountUpdate', handleUnreadCountUpdate)
    notificationWebSocket.on('onMessage', handleNewNotification)
  }
})

onUnmounted(() => {
  stopPolling()
  
  // 移除WebSocket回调
  notificationWebSocket.off('onUnreadCountUpdate', handleUnreadCountUpdate)
  notificationWebSocket.off('onMessage', handleNewNotification)
})

// 暴露方法给父组件
defineExpose({
  loadUnreadCount,
  loadRecentNotifications
})
</script>

<style scoped>
.notification-trigger {
  cursor: pointer;
}

.notification-dropdown {
  width: 360px;
  max-height: 500px;
  padding: 0;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title {
  font-weight: 500;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.dropdown-content {
  max-height: 300px;
  overflow-y: auto;
}

.notification-items {
  /* 移除默认样式 */
}

.notification-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.notification-item:hover {
  background-color: #f8f9fa;
}

.notification-item.is-unread {
  background-color: #f0f8ff;
}

.notification-item:last-child {
  border-bottom: none;
}

.item-indicator {
  width: 6px;
  height: 6px;
  background-color: #409eff;
  border-radius: 50%;
  margin-right: 10px;
  margin-top: 6px;
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.item-title {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-time {
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
  margin-left: 8px;
}

.item-body {
  margin-bottom: 6px;
}

.item-text {
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
  margin: 0;
  word-break: break-word;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-sender {
  color: #909399;
  font-size: 12px;
}

.dropdown-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

/* 滚动条样式 */
.dropdown-content::-webkit-scrollbar {
  width: 6px;
}

.dropdown-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.dropdown-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.dropdown-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 768px) {
  .notification-dropdown {
    width: 300px;
  }
  
  .item-header {
    flex-direction: column;
    gap: 4px;
  }
  
  .item-time {
    margin-left: 0;
    align-self: flex-start;
  }
}
</style>