<template>
  <div class="task-progress-simple">
    <!-- 主要进度显示 -->
    <div class="progress-main">
      <div class="progress-header">
        <span class="progress-percent">{{ progress }}%</span>
        <span class="progress-status" :class="getStatusClass(status)">{{ status }}</span>
      </div>
      <div class="progress-bar">
        <el-progress
          :percentage="progress"
          :status="getProgressStatus(progress)"
          :stroke-width="8"
          :show-text="false"
        />
      </div>
    </div>
    
    <!-- 工时信息（紧凑显示） -->
    <div class="hours-info" v-if="showDetails && (estimatedHours > 0 || actualHours > 0)">
      <div class="hours-row">
        <span class="hours-label">预估/实际:</span>
        <span class="hours-value">{{ estimatedHours }}h / {{ actualHours }}h</span>
        <span class="hours-ratio" :class="getHoursRatioClass()" v-if="estimatedHours > 0">
          ({{ getHoursRatioText() }})
        </span>
      </div>
    </div>
    
    <!-- 操作按钮（紧凑显示） -->
    <div class="action-buttons" v-if="showActions">
      <el-button size="small" text @click="$emit('update-progress')" class="compact-btn">
        更新进度
      </el-button>
      <el-button size="small" text type="success" @click="$emit('recalculate-progress')" class="compact-btn">
        重新计算
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { ActionIcon } from '@/components/SvgIcons';

const props = defineProps({
  progress: {
    type: Number,
    default: 0
  },
  status: {
    type: String,
    default: '待处理'
  },
  estimatedHours: {
    type: Number,
    default: 0
  },
  actualHours: {
    type: Number,
    default: 0
  },
  showDetails: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update-progress', 'recalculate-progress']);

// 获取进度状态
const getProgressStatus = (progress) => {
  if (progress === 100) return 'success';
  if (progress >= 80) return 'warning';
  return null;
};

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    '待处理': 'status-pending',
    '进行中': 'status-inprogress', 
    '已完成': 'status-completed',
    '已取消': 'status-cancelled'
  };
  return statusMap[status] || 'status-default';
};

// 获取工时比例样式
const getHoursRatioClass = () => {
  if (props.estimatedHours === 0) return 'ratio-normal';
  const ratio = props.actualHours / props.estimatedHours;
  if (ratio > 1.3) return 'ratio-danger';
  if (ratio > 1.1) return 'ratio-warning';
  return 'ratio-normal';
};

// 获取工时比例文本
const getHoursRatioText = () => {
  if (props.estimatedHours === 0) return '无预估';
  const ratio = (props.actualHours / props.estimatedHours * 100).toFixed(0);
  return `${ratio}%`;
};
</script>

<style scoped>
.task-progress-simple {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #fff;
  border-radius: 6px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  min-width: 200px;
  max-width: 240px;
}

.progress-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-percent {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.progress-status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

.status-pending {
  background: #f3f4f6;
  color: #6b7280;
}

.status-inprogress {
  background: #fef3c7;
  color: #d97706;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-cancelled {
  background: #fecaca;
  color: #dc2626;
}

:deep(.el-progress-bar__outer) {
  border-radius: 4px;
  background-color: #f1f5f9;
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
}

.hours-info {
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.hours-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.hours-label {
  color: #6b7280;
  white-space: nowrap;
}

.hours-value {
  color: #374151;
  font-weight: 500;
}

.hours-ratio {
  font-weight: 600;
  font-size: 11px;
}

.ratio-normal {
  color: #059669;
}

.ratio-warning {
  color: #d97706;
}

.ratio-danger {
  color: #dc2626;
}

.action-buttons {
  display: flex;
  gap: 4px;
  padding-top: 4px;
  border-top: 1px solid #e5e7eb;
}

.compact-btn {
  flex: 1;
  padding: 4px 8px;
  font-size: 11px;
  border-radius: 4px;
  height: auto;
  
  &:hover {
    opacity: 0.8;
  }
}
</style>