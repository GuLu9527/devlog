<template>
  <BaseChart
    :title="title"
    type="doughnut"
    :data="chartData"
    :options="chartOptions"
    :loading="loading"
    :isEmpty="isEmpty"
    height="300px"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getTaskStatistics } from '@/api/statistics'
import { ElMessage } from 'element-plus'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '任务状态分布'
  },
  userId: {
    type: Number,
    default: null
  },
  groupId: {
    type: Number,
    default: null
  },
  projectId: {
    type: Number,
    default: null
  },
  dateRange: {
    type: Array,
    default: null
  }
})

// 响应式数据
const loading = ref(false)
const statisticsData = ref(null)

// 状态颜色映射
const statusColors = {
  '待处理': '#E6A23C',
  '进行中': '#409EFF', 
  '已完成': '#67C23A',
  '审核中': '#909399',
  '已取消': '#F56C6C'
}

// 计算属性
const isEmpty = computed(() => {
  return statisticsData.value === null || 
         Object.keys(statisticsData.value?.statusStatistics || {}).length === 0
})

const chartData = computed(() => {
  if (!statisticsData.value?.statusStatistics) {
    return {
      labels: [],
      datasets: []
    }
  }

  const statusStats = statisticsData.value.statusStatistics
  const labels = Object.keys(statusStats)
  const data = Object.values(statusStats)
  const backgroundColor = labels.map(label => statusColors[label] || '#909399')

  return {
    labels,
    datasets: [
      {
        data,
        backgroundColor,
        borderColor: backgroundColor.map(color => color),
        borderWidth: 2,
        hoverBorderWidth: 3,
        hoverBorderColor: '#fff'
      }
    ]
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right',
      labels: {
        padding: 20,
        usePointStyle: true,
        pointStyle: 'circle',
        font: {
          size: 12
        }
      }
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const label = context.label
          const value = context.parsed
          const total = context.dataset.data.reduce((a, b) => a + b, 0)
          const percentage = ((value / total) * 100).toFixed(1)
          return `${label}: ${value} (${percentage}%)`
        }
      }
    }
  },
  cutout: '60%',
  elements: {
    arc: {
      borderRadius: 4
    }
  }
}))

// 方法
function formatDateRange() {
  if (!props.dateRange || props.dateRange.length !== 2) {
    return {}
  }
  
  return {
    startDate: formatDate(props.dateRange[0]),
    endDate: formatDate(props.dateRange[1])
  }
}

function formatDate(date) {
  if (!date) return null
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...formatDateRange(),
      userId: props.userId,
      groupId: props.groupId,
      projectId: props.projectId
    }
    
    // 过滤掉null值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await getTaskStatistics(params)
    statisticsData.value = response.data
  } catch (error) {
    console.error('获取任务统计数据失败:', error)
    ElMessage.error('获取任务统计数据失败')
    statisticsData.value = null
  } finally {
    loading.value = false
  }
}

// 监听器
watch(() => [props.userId, props.groupId, props.projectId, props.dateRange], () => {
  fetchData()
}, { deep: true })

// 生命周期
onMounted(() => {
  fetchData()
})

// 暴露方法
defineExpose({
  refresh: fetchData,
  getData: () => statisticsData.value
})
</script>

<style scoped>
/* 组件特定样式 */
</style>