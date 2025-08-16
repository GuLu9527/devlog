<template>
  <button
    :class="buttonClasses"
    :disabled="disabled"
    @click="handleClick"
    @mousedown="handleMouseDown"
    @mouseup="handleMouseUp"
    @mouseleave="handleMouseLeave"
  >
    <svg
      :width="svgWidth"
      :height="svgHeight"
      :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
      class="svg-bg"
    >
      <defs>
        <!-- 主要渐变 -->
        <linearGradient :id="`mainGradient-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :stop-color="gradientColors.main.start" />
          <stop offset="100%" :stop-color="gradientColors.main.end" />
        </linearGradient>
        
        <!-- 悬停渐变 -->
        <linearGradient :id="`hoverGradient-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :stop-color="gradientColors.hover.start" />
          <stop offset="100%" :stop-color="gradientColors.hover.end" />
        </linearGradient>
        
        <!-- 按下渐变 -->
        <linearGradient :id="`activeGradient-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :stop-color="gradientColors.active.start" />
          <stop offset="100%" :stop-color="gradientColors.active.end" />
        </linearGradient>
        
        <!-- 阴影滤镜 -->
        <filter :id="`shadow-${uid}`" x="-50%" y="-50%" width="200%" height="200%">
          <feDropShadow 
            dx="0" 
            dy="2" 
            :flood-opacity="shadowOpacity"
            :stdDeviation="shadowBlur"
            :flood-color="shadowColor"
          />
        </filter>
        
        <!-- 顶部高光 -->
        <linearGradient :id="`topHighlight-${uid}`" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stop-color="#ffffff" stop-opacity="0.4" />
          <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
        </linearGradient>
      </defs>
      
      <!-- 主要背景 -->
      <rect
        x="0"
        y="0"
        :width="svgWidth"
        :height="svgHeight"
        :rx="borderRadius"
        :fill="currentFill"
        :filter="`url(#shadow-${uid})`"
        class="button-bg"
      />
      
      <!-- 顶部高光 -->
      <rect
        v-if="showInnerHighlight"
        x="1"
        y="1"
        :width="svgWidth - 2"
        :height="Math.max(svgHeight * 0.4, 6)"
        :rx="borderRadius - 1"
        :fill="`url(#topHighlight-${uid})`"
        class="button-highlight"
      />
    </svg>
    
    <!-- 按钮内容 -->
    <div class="button-content">
      <div v-if="$slots.icon || icon" class="button-icon">
        <slot name="icon">
          <component :is="icon" v-if="icon" />
        </slot>
      </div>
      <span v-if="$slots.default || text" class="button-text">
        <slot>{{ text }}</slot>
      </span>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <svg class="loading-spinner" :width="iconSize" :height="iconSize" viewBox="0 0 50 50">
        <circle
          cx="25"
          cy="25"
          r="20"
          fill="none"
          :stroke="loadingColor"
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
  </button>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// Props定义
const props = defineProps({
  // 按钮文本
  text: {
    type: String,
    default: ''
  },
  // 按钮类型
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'success', 'warning', 'danger', 'info', 'text'].includes(value)
  },
  // 按钮尺寸
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['mini', 'small', 'medium', 'large'].includes(value)
  },
  // 禁用状态
  disabled: {
    type: Boolean,
    default: false
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 图标组件
  icon: {
    type: [String, Object],
    default: null
  },
  // 圆角大小
  round: {
    type: Boolean,
    default: false
  },
  // 圆形按钮
  circle: {
    type: Boolean,
    default: false
  },
  // 纯文本按钮
  plain: {
    type: Boolean,
    default: false
  }
})

// Emits定义
const emits = defineEmits(['click'])

// 响应式数据
const isPressed = ref(false)
const uid = ref('')

// 生成唯一ID
onMounted(() => {
  uid.value = Math.random().toString(36).substr(2, 9)
})

// 尺寸配置
const sizeConfig = computed(() => {
  const configs = {
    mini: {
      height: 24,
      padding: 8,
      fontSize: 12,
      iconSize: 12,
      borderRadius: 6
    },
    small: {
      height: 32,
      padding: 12,
      fontSize: 13,
      iconSize: 14,
      borderRadius: 8
    },
    medium: {
      height: 40,
      padding: 16,
      fontSize: 14,
      iconSize: 16,
      borderRadius: 8
    },
    large: {
      height: 48,
      padding: 20,
      fontSize: 16,
      iconSize: 18,
      borderRadius: 10
    }
  }
  return configs[props.size]
})

// 颜色配置
const colorConfig = computed(() => {
  const configs = {
    primary: {
      main: { start: '#7c7c80', end: '#636366' },
      hover: { start: '#595959', end: '#515154' },
      active: { start: '#48484a', end: '#3a3a3c' },
      text: '#ffffff',
      shadow: '#636366'
    },
    secondary: {
      main: { start: '#98989d', end: '#8e8e93' },
      hover: { start: '#7c7c81', end: '#6d6d70' },
      active: { start: '#5a5a5d', end: '#48484a' },
      text: '#ffffff',
      shadow: '#8e8e93'
    },
    success: {
      main: { start: '#40c965', end: '#34c759' },
      hover: { start: '#34c759', end: '#30b149' },
      active: { start: '#30b149', end: '#28a138' },
      text: '#ffffff',
      shadow: '#34c759'
    },
    warning: {
      main: { start: '#ffab33', end: '#ff9500' },
      hover: { start: '#ff9500', end: '#e8820e' },
      active: { start: '#e8820e', end: '#d16f0a' },
      text: '#ffffff',
      shadow: '#ff9500'
    },
    danger: {
      main: { start: '#ff5545', end: '#ff3b30' },
      hover: { start: '#ff3b30', end: '#e8261a' },
      active: { start: '#e8261a', end: '#d11404' },
      text: '#ffffff',
      shadow: '#ff3b30'
    },
    info: {
      main: { start: '#1a8cff', end: '#007aff' },
      hover: { start: '#007aff', end: '#0056cc' },
      active: { start: '#0056cc', end: '#004099' },
      text: '#ffffff',
      shadow: '#007aff'
    },
    text: {
      main: { start: 'transparent', end: 'transparent' },
      hover: { start: '#f2f2f7', end: '#e5e5ea' },
      active: { start: '#d1d1d6', end: '#c7c7cc' },
      text: '#1d1d1f',
      shadow: 'transparent'
    }
  }
  return configs[props.type]
})

// 计算属性
const svgWidth = computed(() => {
  if (props.circle) return sizeConfig.value.height
  // 如果只有图标没有文本，使用正方形
  if (props.icon && !props.text) return sizeConfig.value.height
  return Math.max(sizeConfig.value.height, (props.text?.length || 0) * 8 + sizeConfig.value.padding * 2 + (props.icon ? sizeConfig.value.iconSize + 8 : 0))
})

const svgHeight = computed(() => sizeConfig.value.height)

const borderRadius = computed(() => {
  if (props.circle) return sizeConfig.value.height / 2
  if (props.round) return sizeConfig.value.height / 2
  return sizeConfig.value.borderRadius
})

const iconSize = computed(() => sizeConfig.value.iconSize)

const gradientColors = computed(() => colorConfig.value)

const currentFill = computed(() => {
  if (props.disabled) return '#d1d1d6'
  if (isPressed.value) return `url(#activeGradient-${uid.value})`
  return `url(#mainGradient-${uid.value})`
})

const shadowOpacity = computed(() => {
  if (props.disabled || props.type === 'text') return 0
  if (isPressed.value) return 0.1
  return 0.25
})

const shadowBlur = computed(() => isPressed.value ? 2 : 4)

const shadowColor = computed(() => colorConfig.value.shadow)

const loadingColor = computed(() => colorConfig.value.text)

const showInnerHighlight = computed(() => {
  return !props.disabled && props.type !== 'text' && !props.plain
})

const buttonClasses = computed(() => [
  'svg-button',
  `svg-button--${props.type}`,
  `svg-button--${props.size}`,
  {
    'is-disabled': props.disabled,
    'is-loading': props.loading,
    'is-pressed': isPressed.value,
    'is-circle': props.circle,
    'is-round': props.round,
    'is-plain': props.plain
  }
])

// 事件处理
const handleClick = (event) => {
  if (props.disabled || props.loading) return
  emits('click', event)
}

const handleMouseDown = () => {
  if (props.disabled || props.loading) return
  isPressed.value = true
}

const handleMouseUp = () => {
  isPressed.value = false
}

const handleMouseLeave = () => {
  isPressed.value = false
}
</script>

<style scoped>
.svg-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  outline: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  vertical-align: middle;
  -webkit-tap-highlight-color: transparent;
}

.svg-button:hover {
  transform: translateY(-1px);
}

.svg-button:active {
  transform: translateY(0);
}

.svg-button.is-disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.svg-button.is-disabled:hover {
  transform: none;
}

.svg-button.is-loading {
  cursor: not-allowed;
}

.svg-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.button-bg {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.svg-button:hover .button-bg {
  fill: url(#hoverGradient);
}

.inner-highlight {
  transition: opacity 0.3s ease;
}

.button-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  pointer-events: none;
  color: v-bind('colorConfig.text');
  font-size: v-bind('sizeConfig.fontSize + "px"');
  font-weight: 500;
  line-height: 1;
}

.button-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: v-bind('iconSize + "px"');
  height: v-bind('iconSize + "px"');
}

.button-icon svg {
  width: 100%;
  height: 100%;
}

.button-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.svg-button.is-circle .button-text {
  display: none;
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
  z-index: 3;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
  border-radius: inherit;
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

/* 尺寸特定样式 */
.svg-button--mini .button-content {
  padding: 0 6px;
}

.svg-button--small .button-content {
  padding: 0 8px;
}

.svg-button--medium .button-content {
  padding: 0 12px;
}

.svg-button--large .button-content {
  padding: 0 16px;
}

/* 文本按钮特殊样式 */
.svg-button--text {
  background: none !important;
}

.svg-button--text .button-content {
  color: #1d1d1f;
}

.svg-button--text:hover .button-content {
  color: #636366;
}

/* 朴素按钮样式 */
.svg-button.is-plain .button-bg {
  fill: transparent;
  stroke: v-bind('colorConfig.main.start');
  stroke-width: 1;
}

.svg-button.is-plain .button-content {
  color: v-bind('colorConfig.main.start');
}

.svg-button.is-plain:hover .button-bg {
  fill: v-bind('colorConfig.main.start');
}

.svg-button.is-plain:hover .button-content {
  color: #ffffff;
}
</style>