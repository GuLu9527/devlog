<template>
  <div class="notification-list">
    <!-- 工具栏 -->
    <div class="notification-toolbar">
      <div class="toolbar-left">
        <el-select v-model="filters.isRead" placeholder="读取状态" clearable style="width: 120px">
          <el-option :value="0" label="未读" />
          <el-option :value="1" label="已读" />
        </el-select>
        <el-select v-model="filters.type" placeholder="通知类型" clearable style="width: 150px">
          <el-option value="TASK_ASSIGN" label="任务分配" />
          <el-option value="WORKLOG_REVIEW" label="日志审核" />
          <el-option value="TASK_DEADLINE" label="任务截止" />
          <el-option value="PROJECT_UPDATE" label="项目更新" />
          <el-option value="SYSTEM_MESSAGE" label="系统消息" />
        </el-select>
        <el-button @click="refreshNotifications" :icon="RefreshRight">刷新</el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="markAllAsRead" :disabled="!hasUnread">全部已读</el-button>
        <el-button @click="deleteSelectedNotifications" :disabled="!hasSelected" type="danger">删除选中</el-button>
      </div>
    </div>

    <!-- 通知列表 -->
    <div class="notification-content" v-loading="loading">
      <el-empty v-if="!notifications.length && !loading" description="暂无通知" />
      
      <div v-else class="notification-items">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          class="notification-item"
          :class="{
            'is-unread': notification.isRead === 0,
            'is-selected': selectedIds.includes(notification.id)
          }"
          @click="toggleSelect(notification.id)"
        >
          <div class="notification-checkbox">
            <el-checkbox 
              :model-value="selectedIds.includes(notification.id)"
              @change="toggleSelect(notification.id)"
              @click.stop
            />
          </div>
          
          <div class="notification-content-wrapper">
            <div class="notification-header">
              <div class="notification-title">
                <span class="title-text">{{ notification.title }}</span>
                <el-tag 
                  :type="getNotificationTypeTag(notification.type)"
                  size="small"
                  class="type-tag"
                >
                  {{ getNotificationTypeText(notification.type) }}
                </el-tag>
                <el-tag 
                  v-if="notification.level === '紧急'"
                  type="danger"
                  size="small"
                  class="level-tag"
                >
                  {{ notification.level }}
                </el-tag>
                <el-tag 
                  v-else-if="notification.level === '重要'"
                  type="warning"
                  size="small"
                  class="level-tag"
                >
                  {{ notification.level }}
                </el-tag>
              </div>
              <div class="notification-meta">
                <span class="sender">{{ notification.senderName || '系统' }}</span>
                <span class="time">{{ formatTime(notification.createTime) }}</span>
              </div>
            </div>
            
            <div class="notification-body">
              <p class="content">{{ notification.content }}</p>
            </div>
            
            <div class="notification-actions">
              <el-button 
                v-if="notification.isRead === 0"
                @click.stop="markAsRead([notification.id])"
                size="small"
                type="primary"
                link
              >
                标记已读
              </el-button>
              <el-button 
                @click.stop="deleteNotification(notification.id)"
                size="small"
                type="danger"
                link
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="notification-pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'
import { 
  getUserNotifications, 
  markNotificationsAsRead, 
  markAllNotificationsAsRead,
  deleteNotifications 
} from '@/api/notification'

// 响应式数据
const loading = ref(false)
const notifications = ref([])
const selectedIds = ref([])
const total = ref(0)

const pagination = reactive({
  pageNum: 1,
  pageSize: 20
})

const filters = reactive({
  isRead: null,
  type: null
})

// 计算属性
const hasUnread = computed(() => 
  notifications.value.some(n => n.isRead === 0)
)

const hasSelected = computed(() => 
  selectedIds.value.length > 0
)

// 方法
const loadNotifications = async () => {
  loading.value = true
  try {
    const params = {
      ...pagination,
      ...filters
    }
    const response = await getUserNotifications(params)
    
    if (response.data) {
      notifications.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载通知失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const refreshNotifications = () => {
  selectedIds.value = []
  loadNotifications()
}

const toggleSelect = (id) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const markAsRead = async (ids) => {
  try {
    await markNotificationsAsRead(ids)
    ElMessage.success('标记已读成功')
    await loadNotifications()
  } catch (error) {
    ElMessage.error('标记已读失败: ' + (error.message || '未知错误'))
  }
}

const markAllAsRead = async () => {
  try {
    await markAllNotificationsAsRead()
    ElMessage.success('全部标记已读成功')
    await loadNotifications()
  } catch (error) {
    ElMessage.error('标记已读失败: ' + (error.message || '未知错误'))
  }
}

const deleteNotification = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除这条通知吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNotifications([id])
    ElMessage.success('删除成功')
    await loadNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

const deleteSelectedNotifications = async () => {
  if (!selectedIds.value.length) return
  
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 条通知吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNotifications(selectedIds.value)
    ElMessage.success('删除成功')
    selectedIds.value = []
    await loadNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadNotifications()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadNotifications()
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
  } else if (diff < 604800000) { // 小于7天
    return Math.floor(diff / 86400000) + '天前'
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

// 监听过滤条件变化
watch(filters, () => {
  pagination.pageNum = 1
  loadNotifications()
}, { deep: true })

// 组件挂载时加载数据
onMounted(() => {
  loadNotifications()
})

// 暴露方法给父组件
defineExpose({
  refreshNotifications,
  loadNotifications
})
</script>

<style scoped>
.notification-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.notification-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.notification-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.notification-items {
  flex: 1;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:hover {
  background-color: #f8f9fa;
}

.notification-item.is-unread {
  background-color: #f0f8ff;
  border-left: 3px solid #409eff;
}

.notification-item.is-selected {
  background-color: #e8f4f8;
}

.notification-checkbox {
  margin-right: 12px;
  padding-top: 2px;
}

.notification-content-wrapper {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.title-text {
  font-weight: 500;
  color: #303133;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
}

.notification-body {
  margin-bottom: 12px;
}

.content {
  color: #606266;
  line-height: 1.5;
  margin: 0;
  word-break: break-word;
}

.notification-actions {
  display: flex;
  gap: 8px;
}

.notification-pagination {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  display: flex;
  justify-content: center;
}

.type-tag, .level-tag {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .notification-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .toolbar-left, .toolbar-right {
    justify-content: center;
  }
  
  .notification-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .notification-meta {
    align-self: flex-start;
  }
}
</style>