<template>
  <BaseChart
    :title="title"
    type="bar"
    :data="chartData"
    :options="chartOptions"
    :loading="loading"
    :isEmpty="isEmpty"
    height="350px"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getProjectStatistics } from '@/api/statistics'
import { ElMessage } from 'element-plus'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '项目进度统计'
  },
  projectId: {
    type: Number,
    default: null
  },
  departmentId: {
    type: Number,
    default: null
  }
})

// 响应式数据
const loading = ref(false)
const statisticsData = ref(null)

// 计算属性
const isEmpty = computed(() => {
  return statisticsData.value === null || 
         !statisticsData.value?.projectProgress ||
         statisticsData.value.projectProgress.length === 0
})

const chartData = computed(() => {
  if (!statisticsData.value?.projectProgress) {
    return {
      labels: [],
      datasets: []
    }
  }

  const projects = statisticsData.value.projectProgress
  const labels = projects.map(p => p.projectName || `项目${p.projectId}`)
  const completionData = projects.map(p => p.completionRate || 0)
  const averageProgressData = projects.map(p => p.averageProgress || 0)

  return {
    labels,
    datasets: [
      {
        label: '完成率 (%)',
        data: completionData,
        backgroundColor: 'rgba(64, 158, 255, 0.8)',
        borderColor: '#409EFF',
        borderWidth: 1,
        borderRadius: 4,
        borderSkipped: false,
      },
      {
        label: '平均进度 (%)',
        data: averageProgressData,
        backgroundColor: 'rgba(103, 194, 58, 0.8)',
        borderColor: '#67C23A',
        borderWidth: 1,
        borderRadius: 4,
        borderSkipped: false,
      }
    ]
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
      labels: {
        usePointStyle: true,
        pointStyle: 'rect',
        padding: 20
      }
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      callbacks: {
        label: function(context) {
          return `${context.dataset.label}: ${context.parsed.y.toFixed(1)}%`
        }
      }
    }
  },
  scales: {
    x: {
      display: true,
      title: {
        display: true,
        text: '项目'
      },
      grid: {
        display: false
      }
    },
    y: {
      display: true,
      title: {
        display: true,
        text: '百分比 (%)'
      },
      beginAtZero: true,
      max: 100,
      grid: {
        color: 'rgba(0, 0, 0, 0.1)'
      },
      ticks: {
        callback: function(value) {
          return value + '%'
        }
      }
    }
  },
  elements: {
    bar: {
      borderRadius: 4
    }
  }
}))

// 方法
async function fetchData() {
  loading.value = true
  try {
    const params = {
      projectId: props.projectId,
      departmentId: props.departmentId
    }
    
    // 过滤掉null值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await getProjectStatistics(params)
    statisticsData.value = response.data
  } catch (error) {
    console.error('获取项目统计数据失败:', error)
    ElMessage.error('获取项目统计数据失败')
    statisticsData.value = null
  } finally {
    loading.value = false
  }
}

// 监听器
watch(() => [props.projectId, props.departmentId], () => {
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