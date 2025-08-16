<template>
  <div :class="cardClasses" @click="handleClick" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
    <svg
      :width="svgWidth"
      :height="svgHeight"
      :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
      class="card-bg"
    >
      <defs>
        <!-- 背景渐变 -->
        <linearGradient :id="`bgGradient-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :stop-color="backgroundColors.start" />
          <stop offset="100%" :stop-color="backgroundColors.end" />
        </linearGradient>
        
        <!-- 边框渐变 -->
        <linearGradient :id="`borderGradient-${uid}`" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" :stop-color="borderColors.start" />
          <stop offset="100%" :stop-color="borderColors.end" />
        </linearGradient>
        
        <!-- 悬停渐变 -->
        <linearGradient :id="`hoverGradient-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :stop-color="hoverColors.start" />
          <stop offset="100%" :stop-color="hoverColors.end" />
        </linearGradient>
        
        <!-- 阴影滤镜 */
        <filter :id="`cardShadow-${uid}`" x="-50%" y="-50%" width="200%" height="200%">
          <feDropShadow 
            dx="0" 
            :dy="shadowOffset"
            :flood-opacity="shadowOpacity"
            :stdDeviation="shadowBlur"
            flood-color="#000000"
          />
        </filter>
        
        <!-- 内阴影滤镜 -->
        <filter :id="`innerShadow-${uid}`">
          <feFlood flood-color="#ffffff" flood-opacity="0.5"/>
          <feComposite in="SourceGraphic" operator="out"/>
          <feGaussianBlur stdDeviation="1"/>
          <feOffset dx="0" dy="-1"/>
          <feComposite in="SourceGraphic" operator="over"/>
        </filter>
        
        <!-- 内部高光渐变 -->
        <linearGradient :id="`innerHighlight-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stop-color="#ffffff" stop-opacity="0.8" />
          <stop offset="100%" stop-color="#ffffff" stop-opacity="0.2" />
        </linearGradient>
        
        <!-- 发光效果 -->
        <filter :id="`glow-${uid}`" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="4" result="coloredBlur"/>
          <feMerge> 
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <!-- 主背景 -->
      <rect
        x="1"
        y="1"
        :width="svgWidth - 2"
        :height="svgHeight - 2"
        :rx="borderRadius"
        :fill="currentBackground"
        :filter="`url(#cardShadow-${uid})`"
        class="card-background"
      />
      
      <!-- 边框 -->
      <rect
        x="0.5"
        y="0.5"
        :width="svgWidth - 1"
        :height="svgHeight - 1"
        :rx="borderRadius"
        fill="none"
        :stroke="`url(#borderGradient-${uid})`"
        :stroke-width="borderWidth"
        class="card-border"
      />
      
      <!-- 内部高光 -->
      <rect
        v-if="showInnerHighlight"
        x="2"
        y="2"
        :width="svgWidth - 4"
        :height="Math.max(svgHeight / 4, 8)"
        :rx="borderRadius - 2"
        :fill="`url(#innerHighlight-${uid})`"
        opacity="0.3"
        class="inner-highlight"
      />
      
      <!-- 发光边框（悬停时） -->
      <rect
        v-if="glowOnHover && isHovered"
        x="0"
        y="0"
        :width="svgWidth"
        :height="svgHeight"
        :rx="borderRadius + 1"
        fill="none"
        :stroke="glowColor"
        stroke-width="2"
        opacity="0.6"
        :filter="`url(#glow-${uid})`"
        class="glow-border"
      />
    </svg>
    
    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 头部 -->
      <div v-if="$slots.header || title" class="card-header">
        <slot name="header">
          <div class="card-title-wrapper">
            <div v-if="icon" class="card-icon">
              <component :is="icon" :size="iconSize" />
            </div>
            <h3 class="card-title">{{ title }}</h3>
            <div v-if="subtitle" class="card-subtitle">{{ subtitle }}</div>
          </div>
        </slot>
        <div v-if="$slots.actions" class="card-actions">
          <slot name="actions"></slot>
        </div>
      </div>
      
      <!-- 主体内容 -->
      <div class="card-body">
        <slot></slot>
      </div>
      
      <!-- 底部 -->
      <div v-if="$slots.footer" class="card-footer">
        <slot name="footer"></slot>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <svg class="loading-spinner" width="32" height="32" viewBox="0 0 50 50">
        <circle
          cx="25"
          cy="25"
          r="20"
          fill="none"
          stroke="#636366"
          stroke-width="4"
          stroke-linecap="round"
          stroke-dasharray="31.416"
          stroke-dashoffset="31.416"
        >
          <animate
            attributeName="stroke-dasharray"
            dur="2s"
            values="0 31.416;15.708 15.708;0 31.416"
            repeatCount="indefinite"
          />
          <animate
            attributeName="stroke-dashoffset"
            dur="2s"
            values="0;-15.708;-31.416"
            repeatCount="indefinite"
          />
        </circle>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// Props定义
const props = defineProps({
  // 卡片标题
  title: {
    type: String,
    default: ''
  },
  // 卡片副标题
  subtitle: {
    type: String,
    default: ''
  },
  // 图标
  icon: {
    type: [String, Object],
    default: null
  },
  // 卡片类型
  type: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
  },
  // 卡片大小
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: false
  },
  // 是否悬停发光
  glowOnHover: {
    type: Boolean,
    default: false
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 阴影级别
  shadow: {
    type: String,
    default: 'medium',
    validator: (value) => ['none', 'small', 'medium', 'large'].includes(value)
  },
  // 是否显示边框
  bordered: {
    type: Boolean,
    default: true
  },
  // 自定义宽度
  width: {
    type: [String, Number],
    default: null
  },
  // 自定义高度
  height: {
    type: [String, Number],
    default: null
  }
})

// Emits定义
const emits = defineEmits(['click'])

// 响应式数据
const isHovered = ref(false)
const uid = ref('')

// 生成唯一ID
onMounted(() => {
  uid.value = Math.random().toString(36).substr(2, 9)
})

// 尺寸配置
const sizeConfig = computed(() => {
  const configs = {
    small: {
      padding: 16,
      borderRadius: 8,
      iconSize: 16,
      titleSize: 14,
      subtitleSize: 12,
      minHeight: 80
    },
    medium: {
      padding: 20,
      borderRadius: 12,
      iconSize: 20,
      titleSize: 16,
      subtitleSize: 14,
      minHeight: 120
    },
    large: {
      padding: 24,
      borderRadius: 16,
      iconSize: 24,
      titleSize: 18,
      subtitleSize: 16,
      minHeight: 160
    }
  }
  return configs[props.size]
})

// 颜色配置
const colorConfig = computed(() => {
  const configs = {
    default: {
      background: { start: '#ffffff', end: '#fafafa' },
      border: { start: '#d1d1d6', end: '#c7c7cc' },
      hover: { start: '#f8f9fa', end: '#f2f2f7' },
      glow: '#636366'
    },
    primary: {
      background: { start: '#ffffff', end: '#f8f9ff' },
      border: { start: '#636366', end: '#5a5a5d' },
      hover: { start: '#f5f5f7', end: '#ebebf0' },
      glow: '#636366'
    },
    success: {
      background: { start: '#ffffff', end: '#f0fff4' },
      border: { start: '#34c759', end: '#30b149' },
      hover: { start: '#f0fff4', end: '#e6ffed' },
      glow: '#34c759'
    },
    warning: {
      background: { start: '#ffffff', end: '#fffbf0' },
      border: { start: '#ff9500', end: '#e8820e' },
      hover: { start: '#fffbf0', end: '#fff7e6' },
      glow: '#ff9500'
    },
    danger: {
      background: { start: '#ffffff', end: '#fff5f5' },
      border: { start: '#ff3b30', end: '#e8261a' },
      hover: { start: '#fff5f5', end: '#ffebee' },
      glow: '#ff3b30'
    },
    info: {
      background: { start: '#ffffff', end: '#f0f9ff' },
      border: { start: '#007aff', end: '#0056cc' },
      hover: { start: '#f0f9ff', end: '#e6f3ff' },
      glow: '#007aff'
    }
  }
  return configs[props.type]
})

// 阴影配置
const shadowConfig = computed(() => {
  const configs = {
    none: { blur: 0, offset: 0, opacity: 0 },
    small: { blur: 2, offset: 1, opacity: 0.05 },
    medium: { blur: 8, offset: 2, opacity: 0.1 },
    large: { blur: 16, offset: 4, opacity: 0.15 }
  }
  return configs[props.shadow]
})

// SVG尺寸
const svgWidth = computed(() => {
  if (props.width) {
    return typeof props.width === 'number' ? props.width : parseInt(props.width)
  }
  return 320 // 默认宽度
})

const svgHeight = computed(() => {
  if (props.height) {
    return typeof props.height === 'number' ? props.height : parseInt(props.height)
  }
  return Math.max(sizeConfig.value.minHeight, 120) // 默认最小高度
})

const borderRadius = computed(() => sizeConfig.value.borderRadius)
const iconSize = computed(() => sizeConfig.value.iconSize)

// 边框宽度
const borderWidth = computed(() => props.bordered ? 1 : 0)

// 颜色计算
const backgroundColors = computed(() => colorConfig.value.background)
const borderColors = computed(() => colorConfig.value.border)
const hoverColors = computed(() => colorConfig.value.hover)
const glowColor = computed(() => colorConfig.value.glow)

// 当前背景
const currentBackground = computed(() => {
  if (isHovered.value && props.clickable) {
    return `url(#hoverGradient-${uid.value})`
  }
  return `url(#bgGradient-${uid.value})`
})

// 阴影属性
const shadowBlur = computed(() => shadowConfig.value.blur)
const shadowOffset = computed(() => shadowConfig.value.offset)
const shadowOpacity = computed(() => shadowConfig.value.opacity)

// 显示内部高光
const showInnerHighlight = computed(() => {
  return props.type !== 'default' && props.shadow !== 'none'
})

// 卡片类名
const cardClasses = computed(() => [
  'svg-card',
  `svg-card--${props.type}`,
  `svg-card--${props.size}`,
  `svg-card--shadow-${props.shadow}`,
  {
    'is-clickable': props.clickable,
    'is-loading': props.loading,
    'is-hovered': isHovered.value,
    'has-glow': props.glowOnHover
  }
])

// 事件处理
const handleClick = (event) => {
  if (props.loading) return
  emits('click', event)
}

const handleMouseEnter = () => {
  if (!props.loading) {
    isHovered.value = true
  }
}

const handleMouseLeave = () => {
  isHovered.value = false
}
</script>

<style scoped>
.svg-card {
  position: relative;
  display: inline-block;
  width: v-bind('svgWidth + "px"');
  min-height: v-bind('svgHeight + "px"');
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
}

.svg-card.is-clickable {
  cursor: pointer;
}

.svg-card.is-clickable:hover {
  transform: translateY(-2px);
}

.svg-card.is-loading {
  pointer-events: none;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.card-background {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-border {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.glow-border {
  animation: glowPulse 2s ease-in-out infinite alternate;
}

@keyframes glowPulse {
  from {
    opacity: 0.6;
  }
  to {
    opacity: 0.9;
  }
}

.card-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: v-bind('sizeConfig.padding + "px"');
  box-sizing: border-box;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title-wrapper {
  flex: 1;
  min-width: 0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 4px 0;
  font-size: v-bind('sizeConfig.titleSize + "px"');
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.4;
}

.card-icon {
  display: flex;
  align-items: center;
  color: v-bind('colorConfig.glow');
}

.card-subtitle {
  font-size: v-bind('sizeConfig.subtitleSize + "px"');
  color: #8e8e93;
  font-weight: 400;
  line-height: 1.4;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.card-body {
  flex: 1;
  color: #1d1d1f;
  font-size: 14px;
  line-height: 1.6;
}

.card-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f2f2f7;
  color: #8e8e93;
  font-size: 13px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: inherit;
  z-index: 3;
}

.loading-spinner {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 悬停效果 */
.svg-card:hover .card-background {
  fill: url(#hoverGradient);
}

/* 尺寸变体 */
.svg-card--small .card-title {
  font-size: 14px;
}

.svg-card--small .card-subtitle {
  font-size: 12px;
}

.svg-card--large .card-title {
  font-size: 18px;
}

.svg-card--large .card-subtitle {
  font-size: 16px;
}

/* 类型变体 */
.svg-card--primary .card-title {
  color: #636366;
}

.svg-card--success .card-title {
  color: #34c759;
}

.svg-card--warning .card-title {
  color: #ff9500;
}

.svg-card--danger .card-title {
  color: #ff3b30;
}

.svg-card--info .card-title {
  color: #007aff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .svg-card {
    width: 100% !important;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .card-actions {
    justify-content: flex-end;
  }
}
</style>