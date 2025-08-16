import request from '@/utils/request'

/**
 * 获取用户通知列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 页大小
 * @param {number} params.isRead 是否已读(0未读 1已读)
 * @param {string} params.type 通知类型
 * @returns {Promise}
 */
export function getUserNotifications(params) {
  return request({
    url: '/notifications/list',
    method: 'get',
    params
  })
}

/**
 * 获取未读通知数量
 * @returns {Promise}
 */
export function getUnreadCount() {
  return request({
    url: '/notifications/unread-count',
    method: 'get'
  })
}

/**
 * 标记通知为已读
 * @param {Array} notificationIds 通知ID列表
 * @returns {Promise}
 */
export function markNotificationsAsRead(notificationIds) {
  return request({
    url: '/notifications/mark-read',
    method: 'put',
    data: notificationIds
  })
}

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export function markAllNotificationsAsRead() {
  return request({
    url: '/notifications/mark-all-read',
    method: 'put'
  })
}

/**
 * 删除通知
 * @param {Array} notificationIds 通知ID列表
 * @returns {Promise}
 */
export function deleteNotifications(notificationIds) {
  return request({
    url: '/notifications/delete',
    method: 'delete',
    data: notificationIds
  })
}

/**
 * 发送通知
 * @param {Object} notification 通知数据
 * @returns {Promise}
 */
export function sendNotification(notification) {
  return request({
    url: '/notifications/send',
    method: 'post',
    data: notification
  })
}

/**
 * 批量发送通知
 * @param {Array} receiverIds 接收人ID列表
 * @param {Object} notification 通知数据
 * @returns {Promise}
 */
export function batchSendNotification(receiverIds, notification) {
  const requestData = {
    receiverIds: receiverIds,
    notification: notification
  }
  
  console.log('批量发送通知请求数据：', requestData)
  
  return request({
    url: '/notifications/batch-send',
    method: 'post',
    data: requestData
  })
}

/**
 * 发送测试通知
 * @param {number} receiverId 接收人ID
 * @returns {Promise}
 */
export function sendTestNotification(receiverId) {
  return request({
    url: '/notifications/test',
    method: 'post',
    params: { receiverId }
  })
}