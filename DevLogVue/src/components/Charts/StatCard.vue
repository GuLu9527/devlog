<template>
  <div class="stat-card" :class="{ 'is-loading': loading }">
    <div class="stat-card-icon" :style="{ backgroundColor: iconBgColor }">
      <el-icon :size="24" :color="iconColor">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="stat-card-content">
      <div class="stat-card-value">
        <span v-if="loading" class="loading-placeholder">--</span>
        <span v-else>{{ displayValue }}</span>
      </div>
      <div class="stat-card-label">{{ label }}</div>
      <div class="stat-card-extra" v-if="extra">
        <span class="extra-text" :class="{ 
          'is-positive': trendType === 'positive',
          'is-negative': trendType === 'negative'
        }">
          {{ extra }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    default: 0
  },
  icon: {
    type: [String, Object],
    required: true
  },
  iconColor: {
    type: String,
    default: '#fff'
  },
  iconBgColor: {
    type: String,
    default: '#409EFF'
  },
  suffix: {
    type: String,
    default: ''
  },
  precision: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  },
  extra: {
    type: String,
    default: ''
  },
  trendType: {
    type: String,
    default: '', // 'positive', 'negative', ''
    validator: (value) => ['', 'positive', 'negative'].includes(value)
  }
})

// 计算属性
const displayValue = computed(() => {
  if (props.loading) return '--'
  
  let value = props.value
  if (typeof value === 'number') {
    if (props.precision > 0) {
      value = value.toFixed(props.precision)
    } else {
      value = Math.round(value)
    }
    
    // 添加千分位分隔符
    value = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  }
  
  return value + props.suffix
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: default;
}

.stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stat-card.is-loading {
  opacity: 0.6;
}

.stat-card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-card-content {
  flex: 1;
  min-width: 0;
}

.stat-card-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 4px;
}

.loading-placeholder {
  color: #C0C4CC;
}

.stat-card-label {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
  margin-bottom: 2px;
}

.stat-card-extra {
  font-size: 12px;
  line-height: 1.4;
}

.extra-text {
  color: #909399;
}

.extra-text.is-positive {
  color: #67C23A;
}

.extra-text.is-negative {
  color: #F56C6C;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-card {
    padding: 16px;
  }
  
  .stat-card-icon {
    width: 48px;
    height: 48px;
    margin-right: 12px;
  }
  
  .stat-card-value {
    font-size: 24px;
  }
  
  .stat-card-label {
    font-size: 13px;
  }
}
</style>