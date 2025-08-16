<template>
  <div class="chart-container">
    <div class="chart-header" v-if="title">
      <h3 class="chart-title">{{ title }}</h3>
      <div class="chart-actions" v-if="$slots.actions">
        <slot name="actions"></slot>
      </div>
    </div>
    <div class="chart-content" :style="{ height: height }">
      <canvas :ref="chartRef" :id="chartId"></canvas>
    </div>
    <div class="chart-loading" v-if="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div class="chart-empty" v-if="!loading && isEmpty">
      <el-empty description="暂无数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { Chart, registerables } from 'chart.js'

// 注册Chart.js组件
Chart.register(...registerables)

// Props
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    required: true,
    validator: (value) => ['line', 'bar', 'pie', 'doughnut', 'radar', 'polarArea'].includes(value)
  },
  data: {
    type: Object,
    required: true
  },
  options: {
    type: Object,
    default: () => ({})
  },
  height: {
    type: String,
    default: '300px'
  },
  loading: {
    type: Boolean,
    default: false
  },
  isEmpty: {
    type: Boolean,
    default: false
  }
})

// 生成唯一的图表ID
const chartId = ref(`chart-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`)
const chartRef = ref()
let chartInstance = null

// 默认配置
const defaultOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
    tooltip: {
      mode: 'index',
      intersect: false,
    }
  },
  scales: {
    y: {
      beginAtZero: true
    }
  }
}

// 创建图表
const createChart = async () => {
  if (chartInstance) {
    chartInstance.destroy()
  }

  await nextTick()
  
  const canvas = document.getElementById(chartId.value)
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  
  // 合并默认配置和传入的配置
  const mergedOptions = {
    ...defaultOptions,
    ...props.options
  }

  chartInstance = new Chart(ctx, {
    type: props.type,
    data: props.data,
    options: mergedOptions
  })
}

// 更新图表数据
const updateChart = () => {
  if (chartInstance) {
    chartInstance.data = props.data
    chartInstance.update('active')
  }
}

// 监听数据变化
watch(() => props.data, () => {
  updateChart()
}, { deep: true })

watch(() => props.options, () => {
  createChart()
}, { deep: true })

// 生命周期
onMounted(() => {
  if (!props.loading && !props.isEmpty) {
    createChart()
  }
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.destroy()
  }
})

// 暴露方法
defineExpose({
  updateChart,
  getChart: () => chartInstance
})
</script>

<style scoped>
.chart-container {
  position: relative;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.chart-content {
  position: relative;
}

.chart-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.chart-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>