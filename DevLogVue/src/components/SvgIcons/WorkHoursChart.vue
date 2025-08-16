<template>
  <div class="work-hours-chart">
    <div class="chart-header">
      <h3>工时统计概览</h3>
      <div class="chart-controls">
        <el-button-group size="small">
          <el-button 
            :type="timeRange === 'week' ? 'primary' : ''" 
            @click="timeRange = 'week'"
          >
            本周
          </el-button>
          <el-button 
            :type="timeRange === 'month' ? 'primary' : ''" 
            @click="timeRange = 'month'"
          >
            本月
          </el-button>
          <el-button 
            :type="timeRange === 'quarter' ? 'primary' : ''" 
            @click="timeRange = 'quarter'"
          >
            本季度
          </el-button>
        </el-button-group>
      </div>
    </div>
    
    <div class="chart-content">
      <!-- SVG图表 -->
      <svg width="100%" height="200" viewBox="0 0 400 200" class="hours-chart-svg">
        <defs>
          <linearGradient id="hoursGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stop-color="#64748b" stop-opacity="0.6"/>
            <stop offset="100%" stop-color="#64748b" stop-opacity="0.1"/>
          </linearGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
            <feMerge> 
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
        </defs>
        
        <!-- 背景网格 -->
        <g class="grid" stroke="#e2e8f0" stroke-width="0.5">
          <line v-for="i in 5" :key="'h-' + i" 
                :x1="40" :y1="30 + i * 30" 
                :x2="380" :y2="30 + i * 30" />
          <line v-for="i in 8" :key="'v-' + i" 
                :x1="50 + i * 40" :y1="30" 
                :x2="50 + i * 40" :y2="180" />
        </g>
        
        <!-- Y轴标签 -->
        <g class="y-axis-labels" fill="#64748b" font-size="10">
          <text v-for="(label, i) in yAxisLabels" :key="i" 
                :x="35" :y="180 - i * 30" 
                text-anchor="end" dominant-baseline="middle">
            {{ label }}
          </text>
        </g>
        
        <!-- X轴标签 -->
        <g class="x-axis-labels" fill="#64748b" font-size="10">
          <text v-for="(label, i) in xAxisLabels" :key="i" 
                :x="70 + i * 40" :y="195" 
                text-anchor="middle">
            {{ label }}
          </text>
        </g>
        
        <!-- 数据线 -->
        <path 
          :d="chartPath" 
          fill="url(#hoursGradient)" 
          stroke="#475569" 
          stroke-width="2"
          class="chart-line"
        />
        
        <!-- 数据点 -->
        <g class="data-points">
          <circle 
            v-for="(point, i) in dataPoints" 
            :key="i"
            :cx="point.x" 
            :cy="point.y" 
            r="4" 
            fill="#475569" 
            stroke="#ffffff" 
            stroke-width="2"
            class="data-point"
            filter="url(#glow)"
            @mouseover="showTooltip(i, $event)"
            @mouseout="hideTooltip"
          />
        </g>
        
        <!-- 工具提示 -->
        <g v-if="tooltip.visible" class="tooltip">
          <rect 
            :x="tooltip.x - 25" 
            :y="tooltip.y - 35" 
            width="50" 
            height="25" 
            rx="4" 
            fill="#262626" 
            opacity="0.9"
          />
          <text 
            :x="tooltip.x" 
            :y="tooltip.y - 20" 
            fill="white" 
            font-size="12" 
            text-anchor="middle"
          >
            {{ tooltip.text }}
          </text>
        </g>
      </svg>
      
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="#475569" stroke-width="1.5" opacity="0.8"/>
              <polyline points="12,6 12,12 16,14" stroke="#475569" stroke-width="1.5" stroke-linecap="round" opacity="0.9"/>
            </svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ totalHours }}</span>
            <span class="stat-label">总工时</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="#64748b" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.8"/>
            </svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ avgHours }}</span>
            <span class="stat-label">日均工时</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <polyline points="22,12 18,12 15,21 9,3 6,12 2,12" stroke="#475569" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.8"/>
            </svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ efficiency }}%</span>
            <span class="stat-label">效率指数</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => [
      { date: '周一', hours: 8.5 },
      { date: '周二', hours: 7.2 },
      { date: '周三', hours: 9.1 },
      { date: '周四', hours: 6.8 },
      { date: '周五', hours: 8.0 },
      { date: '周六', hours: 4.5 },
      { date: '周日', hours: 2.0 }
    ]
  }
})

const timeRange = ref('week')
const tooltip = ref({
  visible: false,
  x: 0,
  y: 0,
  text: ''
})

const xAxisLabels = computed(() => props.data.map(item => item.date))
const yAxisLabels = computed(() => ['0h', '2h', '4h', '6h', '8h', '10h'])

const maxHours = computed(() => Math.max(...props.data.map(item => item.hours), 10))

const dataPoints = computed(() => {
  return props.data.map((item, index) => ({
    x: 70 + index * 40,
    y: 180 - (item.hours / maxHours.value) * 150,
    hours: item.hours
  }))
})

const chartPath = computed(() => {
  if (dataPoints.value.length === 0) return ''
  
  let path = `M 50 180`
  dataPoints.value.forEach((point, index) => {
    if (index === 0) {
      path += ` L ${point.x} ${point.y}`
    } else {
      path += ` L ${point.x} ${point.y}`
    }
  })
  path += ` L ${dataPoints.value[dataPoints.value.length - 1].x} 180 Z`
  
  return path
})

const totalHours = computed(() => {
  return props.data.reduce((sum, item) => sum + item.hours, 0).toFixed(1)
})

const avgHours = computed(() => {
  return (parseFloat(totalHours.value) / props.data.length).toFixed(1)
})

const efficiency = computed(() => {
  const target = 8 * props.data.length
  return Math.min(100, ((parseFloat(totalHours.value) / target) * 100)).toFixed(0)
})

const showTooltip = (index, event) => {
  const point = dataPoints.value[index]
  tooltip.value = {
    visible: true,
    x: point.x,
    y: point.y - 10,
    text: `${point.hours}h`
  }
}

const hideTooltip = () => {
  tooltip.value.visible = false
}
</script>

<style scoped>
.work-hours-chart {
  background: rgba(248, 250, 252, 0.9);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  color: #334155;
  font-size: 18px;
  font-weight: 600;
}

.hours-chart-svg {
  margin-bottom: 20px;
}

.chart-line {
  animation: drawLine 2s ease-in-out;
}

.data-point {
  animation: fadeInScale 0.6s ease-out;
  animation-fill-mode: both;
  cursor: pointer;
  transition: all 0.2s ease;
}

.data-point:hover {
  r: 6;
  filter: url(#glow);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-color: #94a3b8;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(100, 116, 139, 0.08);
  border-radius: 50%;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #334155;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

@keyframes drawLine {
  0% {
    stroke-dasharray: 1000;
    stroke-dashoffset: 1000;
  }
  100% {
    stroke-dasharray: 1000;
    stroke-dashoffset: 0;
  }
}

@keyframes fadeInScale {
  0% {
    opacity: 0;
    transform: scale(0);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 为每个数据点添加延迟动画 */
.data-point:nth-child(1) { animation-delay: 0.2s; }
.data-point:nth-child(2) { animation-delay: 0.4s; }
.data-point:nth-child(3) { animation-delay: 0.6s; }
.data-point:nth-child(4) { animation-delay: 0.8s; }
.data-point:nth-child(5) { animation-delay: 1.0s; }
.data-point:nth-child(6) { animation-delay: 1.2s; }
.data-point:nth-child(7) { animation-delay: 1.4s; }

@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>