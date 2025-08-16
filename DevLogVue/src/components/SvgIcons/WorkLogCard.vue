<template>
  <div class="work-log-card" :class="{ 'selected': selected }">
    <!-- 卡片头部 -->
    <div class="card-header">
      <div class="header-left">
        <StatusIcon 
          :status="getStatusType(workLog.reviewStatus)" 
          :size="24" 
          :animate="workLog.reviewStatus === '待审核'"
        />
        <div class="task-info">
          <h4 class="task-title">{{ workLog.taskTitle }}</h4>
          <span class="task-id">#{{ workLog.id }}</span>
        </div>
      </div>
      <div class="header-right">
        <div class="work-hours">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="#64748b" stroke-width="1.5" opacity="0.6"/>
            <polyline points="12,6 12,12 16,14" stroke="#475569" stroke-width="1.5" stroke-linecap="round" opacity="0.8"/>
          </svg>
          <span>{{ workLog.hours }}h</span>
        </div>
      </div>
    </div>
    
    <!-- 用户信息 -->
    <div class="user-section">
      <div class="user-avatar">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="#64748b" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" opacity="0.7"/>
          <circle cx="12" cy="7" r="4" stroke="#475569" stroke-width="1.5" opacity="0.8"/>
        </svg>
      </div>
      <div class="user-details">
        <span class="user-name">{{ workLog.userName }}</span>
        <span class="log-date">{{ workLog.logDate }}</span>
      </div>
    </div>
    
    <!-- 日志内容 -->
    <div class="content-section">
      <p class="log-content">{{ workLog.content }}</p>
    </div>
    
    <!-- 审核信息 -->
    <div v-if="workLog.reviewStatus !== '待审核'" class="review-section">
      <div class="review-info">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M9 11H5a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h11a2 2 0 0 0 2-2v-7a2 2 0 0 0-2-2h-1M9 11V9a2 2 0 1 1 4 0v2M9 11h6" 
                stroke="#666" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>审核人: {{ workLog.reviewerName || '未知' }}</span>
        <span class="review-time">{{ formatTime(workLog.reviewTime) }}</span>
      </div>
      <div v-if="workLog.reviewComment" class="review-comment">
        <span>"{{ workLog.reviewComment }}"</span>
      </div>
    </div>
    
    <!-- 操作按钮区域 -->
    <div class="action-section">
      <div class="action-buttons">
        <ActionIcon 
          action="view" 
          :size="18" 
          @click="$emit('view', workLog)"
        />
        <ActionIcon 
          v-if="workLog.editable"
          action="edit" 
          :size="18" 
          @click="$emit('edit', workLog)"
        />
        <ActionIcon 
          v-if="workLog.reviewStatus === '待审核'"
          action="approve" 
          :size="18" 
          @click="$emit('approve', workLog)"
        />
        <ActionIcon 
          v-if="workLog.reviewStatus === '待审核'"
          action="reject" 
          :size="18" 
          @click="$emit('reject', workLog)"
        />
        <ActionIcon 
          v-if="workLog.deletable"
          action="delete" 
          :size="18" 
          @click="$emit('delete', workLog)"
        />
      </div>
      <div class="time-info">
        <span class="create-time">{{ formatTime(workLog.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import StatusIcon from './StatusIcon.vue'
import ActionIcon from './ActionIcon.vue'

const props = defineProps({
  workLog: {
    type: Object,
    required: true
  },
  selected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view', 'edit', 'approve', 'reject', 'delete'])

const getStatusType = (reviewStatus) => {
  const statusMap = {
    '待审核': 'pending',
    '已通过': 'approved',
    '已拒绝': 'rejected'
  }
  return statusMap[reviewStatus] || 'default'
}

const formatTime = (timeString) => {
  if (!timeString) return '-'
  const date = new Date(timeString)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.work-log-card {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.work-log-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #64748b, #94a3b8, #cbd5e1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.work-log-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border-color: #94a3b8;
}

.work-log-card:hover::before {
  opacity: 0.6;
}

.work-log-card.selected {
  border-color: #64748b;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
}

.work-log-card.selected::before {
  opacity: 0.8;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-info .task-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  line-height: 1.2;
}

.task-info .task-id {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.header-right .work-hours {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(148, 163, 184, 0.1);
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(148, 163, 184, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(100, 116, 139, 0.08);
  border-radius: 50%;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.log-date {
  font-size: 12px;
  color: #64748b;
}

.content-section {
  margin-bottom: 16px;
}

.log-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #475569;
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #94a3b8;
}

.review-section {
  background: rgba(148, 163, 184, 0.05);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
}

.review-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #475569;
  margin-bottom: 8px;
}

.review-time {
  margin-left: auto;
  color: #64748b;
}

.review-comment {
  font-size: 13px;
  color: #475569;
  font-style: italic;
  padding-left: 22px;
}

.action-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.time-info .create-time {
  font-size: 12px;
  color: #64748b;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .work-log-card {
    padding: 16px;
    margin-bottom: 12px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .header-right {
    align-self: flex-end;
  }
  
  .action-section {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>