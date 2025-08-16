<template>
  <div class="notification-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">通知消息</h1>
          <div class="quick-actions">
            <el-button 
              type="primary" 
              class="action-btn mark-read-btn"
              @click="handleMarkAllRead"
              :disabled="unreadCount === 0"
            >
              <ActionIcon action="check" :size="16" />
              全部已读
            </el-button>
            <NotificationQuickSend ref="quickSendRef" @success="handleSendSuccess" />
          </div>
        </div>
        
        <div class="header-stats">
          <div class="stat-card unread">
            <div class="stat-icon">
              <ActionIcon action="notification" :size="20" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ unreadCount }}</div>
              <div class="stat-label">未读消息</div>
            </div>
          </div>
          <div class="stat-card total">
            <div class="stat-icon">
              <ActionIcon action="list" :size="20" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ totalCount }}</div>
              <div class="stat-label">总消息</div>
            </div>
          </div>
          <div class="stat-card filter">
            <div class="stat-icon">
              <ActionIcon action="filter" :size="20" />
            </div>
            <div class="stat-content">
              <el-select 
                v-model="filterType" 
                placeholder="筛选类型"
                size="small"
                style="width: 120px"
                @change="handleFilterChange"
              >
                <el-option label="全部" value="all" />
                <el-option label="未读" value="unread" />
                <el-option label="已读" value="read" />
                <el-option label="系统通知" value="system" />
                <el-option label="任务通知" value="task" />
              </el-select>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="page-content">
      <el-card class="notification-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <span class="card-title">消息列表</span>
              <el-tag v-if="filterType !== 'all'" :type="getFilterTagType(filterType)" size="small">
                {{ getFilterLabel(filterType) }}
              </el-tag>
            </div>
            <div class="header-right">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索消息内容"
                class="search-input"
                clearable
                size="small"
                @input="handleSearch"
              >
                <template #prefix>
                  <ActionIcon action="search" :size="14" />
                </template>
              </el-input>
            </div>
          </div>
        </template>
        
        <NotificationList 
          ref="notificationListRef" 
          :filter-type="filterType"
          :search-keyword="searchKeyword"
          @update-stats="handleStatsUpdate" 
        />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ActionIcon } from '@/components/SvgIcons'
import NotificationList from '@/components/Notification/NotificationList.vue'
import NotificationQuickSend from '@/components/Notification/NotificationQuickSend.vue'
import { getUnreadCount } from '@/api/notification'

// 响应式数据
const notificationListRef = ref(null)
const quickSendRef = ref(null)
const unreadCount = ref(0)
const totalCount = ref(0)
const filterType = ref('all')
const searchKeyword = ref('')

// 方法
const loadStats = async () => {
  try {
    const response = await getUnreadCount()
    if (response.data) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const handleStatsUpdate = (stats) => {
  // 从子组件接收统计数据更新
  if (stats) {
    unreadCount.value = stats.unreadCount || 0
    totalCount.value = stats.totalCount || 0
  }
}

const handleSendSuccess = () => {
  // 通知发送成功后刷新列表
  if (notificationListRef.value) {
    notificationListRef.value.refreshNotifications()
  }
  loadStats()
}

const handleMarkAllRead = async () => {
  try {
    if (notificationListRef.value) {
      await notificationListRef.value.markAllAsRead()
      ElMessage.success('所有消息已标记为已读')
      loadStats()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const handleFilterChange = () => {
  // 筛选条件改变时刷新列表
  if (notificationListRef.value) {
    notificationListRef.value.refreshNotifications()
  }
}

const handleSearch = () => {
  // 搜索关键词改变时刷新列表
  if (notificationListRef.value) {
    notificationListRef.value.refreshNotifications()
  }
}

const getFilterTagType = (type) => {
  const typeMap = {
    'unread': 'warning',
    'read': 'info',
    'system': 'primary',
    'task': 'success'
  }
  return typeMap[type] || 'info'
}

const getFilterLabel = (type) => {
  const labelMap = {
    'unread': '未读',
    'read': '已读',
    'system': '系统通知',
    'task': '任务通知'
  }
  return labelMap[type] || '全部'
}

// 生命周期
onMounted(() => {
  loadStats()
})
</script>

<style lang="scss" scoped>
.notification-page {
  padding: 20px;
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.page-header {
  margin-bottom: 24px;
  
  .header-content {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(148, 163, 184, 0.1);
    overflow: hidden;
    
    .title-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
      
      .page-title {
        margin: 0;
        color: #1f2937;
        font-size: 26px;
        font-weight: 700;
      }
      
      .quick-actions {
        display: flex;
        gap: 12px;
        align-items: center;
        
        .mark-read-btn {
          border-radius: 12px;
          font-weight: 500;
          padding: 10px 16px;
          gap: 6px;
          background: #28a745;
          border-color: #28a745;
          transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
          
          &:hover:not(:disabled) {
            background: #1e7e34;
            border-color: #1e7e34;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
          }
          
          &:disabled {
            background: #f2f2f7;
            color: #c7c7cc;
            border-color: #e5e5ea;
            transform: none;
            box-shadow: none;
          }
        }
      }
    }
    
    .header-stats {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr;
      gap: 1px;
      background: #e5e7eb;
      
      .stat-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px 24px;
        background: white;
        transition: all 0.3s ease;
        
        &:hover {
          background: #f8f9fa;
        }
        
        .stat-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 48px;
          height: 48px;
          border-radius: 12px;
          background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
          color: #1976d2;
        }
        
        &.unread .stat-icon {
          background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
          color: #d68910;
        }
        
        &.total .stat-icon {
          background: linear-gradient(135deg, #e8f5e8 0%, #c3e6c3 100%);
          color: #28a745;
        }
        
        &.filter .stat-icon {
          background: linear-gradient(135deg, #f0f2f5 0%, #d1d5db 100%);
          color: #6b7280;
        }
        
        .stat-content {
          flex: 1;
          
          .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #1f2937;
            line-height: 1;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 14px;
            color: #6b7280;
            font-weight: 500;
          }
        }
      }
    }
  }
}

.page-content {
  .notification-card {
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(148, 163, 184, 0.1);
    min-height: 500px;
    
    :deep(.el-card__header) {
      background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
      border-bottom: 2px solid #e5e7eb;
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .header-left {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .card-title {
            font-size: 18px;
            font-weight: 600;
            color: #1f2937;
          }
        }
        
        .header-right {
          .search-input {
            width: 280px;
            
            :deep(.el-input__wrapper) {
              border-radius: 20px;
              background: rgba(243, 244, 246, 0.8);
              box-shadow: none;
              border: 1px solid #e5e7eb;
              transition: all 0.3s ease;
              
              &:hover {
                border-color: #007AFF;
                background: white;
              }
              
              &.is-focus {
                border-color: #007AFF;
                background: white;
                box-shadow: 0 0 0 2px rgba(0, 122, 255, 0.2);
              }
            }
          }
        }
      }
    }
  }
}

// iOS风格按钮样式
:deep(.action-btn) {
  border-radius: 12px;
  font-weight: 500;
  padding: 10px 18px;
  gap: 6px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 122, 255, 0.25);
  }
  
  &:active {
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .notification-page {
    padding: 16px;
  }
  
  .page-header {
    .header-content {
      .title-section {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
        padding: 20px;
        
        .page-title {
          font-size: 22px;
        }
      }
      
      .header-stats {
        grid-template-columns: 1fr;
        gap: 1px;
        
        .stat-card {
          padding: 16px 20px;
          
          .stat-icon {
            width: 40px;
            height: 40px;
          }
          
          .stat-content .stat-value {
            font-size: 24px;
          }
        }
      }
    }
  }
  
  .page-content {
    .notification-card {
      :deep(.el-card__header) {
        .card-header {
          flex-direction: column;
          gap: 16px;
          align-items: flex-start;
          
          .header-right .search-input {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>