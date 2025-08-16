<template>
  <BaseChart
    :title="title"
    type="line"
    :data="chartData"
    :options="chartOptions"
    :loading="loading"
    :isEmpty="isEmpty"
    height="350px"
  >
    <template #actions>
      <el-select v-model="dimension" @change="handleDimensionChange" size="small" style="width: 100px;">
        <el-option label="按天" value="day" />
        <el-option label="按周" value="week" />
        <el-option label="按月" value="month" />
      </el-select>
    </template>
  </BaseChart>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getWorkHoursStatistics } from '@/api/statistics'
import { ElMessage } from 'element-plus'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '工时趋势'
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
    default: () => {
      const today = new Date()
      const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
      return [thirtyDaysAgo, today]
    }
  }
})

// 响应式数据
const loading = ref(false)
const dimension = ref('day')
const statisticsData = ref(null)

// 计算属性
const isEmpty = computed(() => {
  return statisticsData.value === null || 
         Object.keys(statisticsData.value?.timeStatistics || {}).length === 0
})

const chartData = computed(() => {
  if (!statisticsData.value?.timeStatistics) {
    return {
      labels: [],
      datasets: []
    }
  }

  const timeStats = statisticsData.value.timeStatistics
  const labels = Object.keys(timeStats).sort()
  const data = labels.map(label => parseFloat(timeStats[label] || 0))

  return {
    labels,
    datasets: [
      {
        label: '工时 (小时)',
        data,
        borderColor: '#409EFF',
        backgroundColor: 'rgba(64, 158, 255, 0.1)',
        tension: 0.4,
        fill: true,
        pointBackgroundColor: '#409EFF',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6
      }
    ]
  }
})

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      callbacks: {
        label: function(context) {
          return `工时: ${context.parsed.y} 小时`
        }
      }
    }
  },
  scales: {
    x: {
      display: true,
      title: {
        display: true,
        text: getDimensionLabel()
      },
      grid: {
        display: false
      }
    },
    y: {
      display: true,
      title: {
        display: true,
        text: '工时 (小时)'
      },
      beginAtZero: true,
      grid: {
        color: 'rgba(0, 0, 0, 0.1)'
      }
    }
  },
  elements: {
    point: {
      hoverBackgroundColor: '#409EFF'
    }
  }
}))

// 方法
function getDimensionLabel() {
  const labels = {
    day: '日期',
    week: '周次', 
    month: '月份'
  }
  return labels[dimension.value] || '时间'
}

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
      projectId: props.projectId,
      dimension: dimension.value
    }
    
    // 过滤掉null值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await getWorkHoursStatistics(params)
    statisticsData.value = response.data
  } catch (error) {
    console.error('获取工时统计数据失败:', error)
    ElMessage.error('获取工时统计数据失败')
    statisticsData.value = null
  } finally {
    loading.value = false
  }
}

function handleDimensionChange() {
  fetchData()
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