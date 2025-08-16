<template>
  <div :class="containerClasses">
    <div class="input-wrapper">
      <svg
        :width="svgWidth"
        :height="svgHeight"
        :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
        class="input-bg"
      >
        <defs>
          <!-- 边框渐变 -->
          <linearGradient :id="`borderGradient-${uid}`" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" :stop-color="borderColors.start" />
            <stop offset="100%" :stop-color="borderColors.end" />
          </linearGradient>
          
          <!-- 焦点渐变 -->
          <linearGradient :id="`focusGradient-${uid}`" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" :stop-color="focusColors.start" />
            <stop offset="100%" :stop-color="focusColors.end" />
          </linearGradient>
          
          <!-- 阴影滤镜 -->
          <filter :id="`inputShadow-${uid}`" x="-50%" y="-50%" width="200%" height="200%">
            <feDropShadow 
              dx="0" 
              dy="1" 
              :flood-opacity="shadowOpacity"
              stdDeviation="2"
              flood-color="#000000"
            />
          </filter>
          
          <!-- 内阴影滤镜 -->
          <filter :id="`innerShadow-${uid}`">
            <feFlood flood-color="#000000" flood-opacity="0.05"/>
            <feComposite in="SourceGraphic" operator="out"/>
            <feGaussianBlur stdDeviation="1"/>
            <feOffset dx="0" dy="1"/>
            <feComposite in="SourceGraphic" operator="over"/>
          </filter>
        </defs>
        
        <!-- 背景 -->
        <rect
          x="1"
          y="1"
          :width="svgWidth - 2"
          :height="svgHeight - 2"
          :rx="borderRadius"
          fill="#ffffff"
          :filter="`url(#innerShadow-${uid})`"
        />
        
        <!-- 边框 -->
        <rect
          x="0.5"
          y="0.5"
          :width="svgWidth - 1"
          :height="svgHeight - 1"
          :rx="borderRadius"
          fill="none"
          :stroke="currentBorderColor"
          stroke-width="1"
          :filter="`url(#inputShadow-${uid})`"
          class="input-border"
        />
        
        <!-- 焦点环 -->
        <rect
          v-if="isFocused"
          x="0"
          y="0"
          :width="svgWidth"
          :height="svgHeight"
          :rx="borderRadius + 1"
          fill="none"
          :stroke="`url(#focusGradient-${uid})`"
          stroke-width="2"
          opacity="0.6"
          class="focus-ring"
        />
      </svg>
      
      <!-- 前置图标 -->
      <div v-if="$slots.prefix || prefixIcon" class="input-prefix">
        <slot name="prefix">
          <component :is="prefixIcon" v-if="prefixIcon" :size="iconSize" />
        </slot>
      </div>
      
      <!-- 输入框 -->
      <input
        ref="inputRef"
        v-model="inputValue"
        :type="inputType"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        :class="inputClasses"
        @focus="handleFocus"
        @blur="handleBlur"
        @input="handleInput"
        @change="handleChange"
        @keydown="handleKeydown"
      />
      
      <!-- 后置图标 -->
      <div v-if="$slots.suffix || suffixIcon || showPasswordIcon || showClearIcon" class="input-suffix">
        <slot name="suffix">
          <!-- 清除按钮 -->
          <button
            v-if="showClearIcon"
            class="clear-btn"
            @click="handleClear"
          >
            <svg :width="iconSize" :height="iconSize" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" fill="#8e8e93"/>
              <path d="m15 9-6 6M9 9l6 6" stroke="white" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          
          <!-- 密码切换按钮 -->
          <button
            v-if="showPasswordIcon"
            class="password-btn"
            @click="togglePasswordVisibility"
          >
            <svg :width="iconSize" :height="iconSize" viewBox="0 0 24 24" fill="none">
              <path
                v-if="showPassword"
                d="M2 12s3-5 10-5 10 5 10 5-3 5-10 5-10-5-10-5z"
                stroke="#8e8e93"
                stroke-width="2"
                fill="none"
              />
              <circle
                v-if="showPassword"
                cx="12"
                cy="12"
                r="3"
                stroke="#8e8e93"
                stroke-width="2"
                fill="none"
              />
              <path
                v-else
                d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
                stroke="#8e8e93"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                fill="none"
              />
              <line
                v-if="!showPassword"
                x1="1"
                y1="1"
                x2="23"
                y2="23"
                stroke="#8e8e93"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>
          </button>
          
          <!-- 普通后置图标 -->
          <component :is="suffixIcon" v-if="suffixIcon" :size="iconSize" />
        </slot>
      </div>
    </div>
    
    <!-- 错误信息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <!-- 帮助文本 -->
    <div v-if="helpText && !errorMessage" class="help-text">
      {{ helpText }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'

// Props定义
const props = defineProps({
  // 输入值
  modelValue: {
    type: [String, Number],
    default: ''
  },
  // 输入框类型
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'password', 'email', 'number', 'tel', 'url', 'search'].includes(value)
  },
  // 占位符
  placeholder: {
    type: String,
    default: ''
  },
  // 尺寸
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  // 禁用状态
  disabled: {
    type: Boolean,
    default: false
  },
  // 只读状态
  readonly: {
    type: Boolean,
    default: false
  },
  // 最大长度
  maxlength: {
    type: Number,
    default: null
  },
  // 前置图标
  prefixIcon: {
    type: [String, Object],
    default: null
  },
  // 后置图标
  suffixIcon: {
    type: [String, Object],
    default: null
  },
  // 是否可清除
  clearable: {
    type: Boolean,
    default: false
  },
  // 显示密码切换
  showPassword: {
    type: Boolean,
    default: false
  },
  // 错误信息
  errorMessage: {
    type: String,
    default: ''
  },
  // 帮助文本
  helpText: {
    type: String,
    default: ''
  },
  // 验证状态
  validateStatus: {
    type: String,
    default: '',
    validator: (value) => ['', 'success', 'warning', 'error'].includes(value)
  }
})

// Emits定义
const emits = defineEmits(['update:modelValue', 'focus', 'blur', 'input', 'change', 'clear', 'keydown'])

// 响应式数据
const inputRef = ref(null)
const isFocused = ref(false)
const showPasswordVisible = ref(false)
const uid = ref('')

// 生成唯一ID
onMounted(() => {
  uid.value = Math.random().toString(36).substr(2, 9)
})

// 输入值双向绑定
const inputValue = computed({
  get: () => props.modelValue,
  set: (value) => emits('update:modelValue', value)
})

// 实际输入类型
const inputType = computed(() => {
  if (props.type === 'password') {
    return showPasswordVisible.value ? 'text' : 'password'
  }
  return props.type
})

// 尺寸配置
const sizeConfig = computed(() => {
  const configs = {
    small: {
      height: 32,
      padding: 8,
      fontSize: 13,
      iconSize: 14,
      borderRadius: 6
    },
    medium: {
      height: 40,
      padding: 12,
      fontSize: 14,
      iconSize: 16,
      borderRadius: 8
    },
    large: {
      height: 48,
      padding: 16,
      fontSize: 16,
      iconSize: 18,
      borderRadius: 10
    }
  }
  return configs[props.size]
})

// SVG尺寸
const svgWidth = computed(() => 200) // 固定宽度，由CSS控制实际宽度
const svgHeight = computed(() => sizeConfig.value.height)
const borderRadius = computed(() => sizeConfig.value.borderRadius)
const iconSize = computed(() => sizeConfig.value.iconSize)

// 边框颜色
const borderColors = computed(() => {
  if (props.disabled) {
    return { start: '#d1d1d6', end: '#d1d1d6' }
  }
  if (props.errorMessage || props.validateStatus === 'error') {
    return { start: '#ff3b30', end: '#e8261a' }
  }
  if (props.validateStatus === 'warning') {
    return { start: '#ff9500', end: '#e8820e' }
  }
  if (props.validateStatus === 'success') {
    return { start: '#34c759', end: '#30b149' }
  }
  return { start: '#d1d1d6', end: '#c7c7cc' }
})

// 焦点颜色
const focusColors = computed(() => {
  if (props.errorMessage || props.validateStatus === 'error') {
    return { start: '#ff3b30', end: '#e8261a' }
  }
  if (props.validateStatus === 'warning') {
    return { start: '#ff9500', end: '#e8820e' }
  }
  if (props.validateStatus === 'success') {
    return { start: '#34c759', end: '#30b149' }
  }
  return { start: '#636366', end: '#5a5a5d' }
})

// 当前边框颜色
const currentBorderColor = computed(() => {
  if (isFocused.value) {
    return `url(#focusGradient-${uid.value})`
  }
  return `url(#borderGradient-${uid.value})`
})

// 阴影透明度
const shadowOpacity = computed(() => {
  if (props.disabled) return 0
  if (isFocused.value) return 0.15
  return 0.05
})

// 显示清除按钮
const showClearIcon = computed(() => {
  return props.clearable && inputValue.value && !props.disabled && !props.readonly
})

// 显示密码切换按钮
const showPasswordIcon = computed(() => {
  return props.showPassword || props.type === 'password'
})

// 容器类名
const containerClasses = computed(() => [
  'svg-input-container',
  `svg-input--${props.size}`,
  {
    'is-disabled': props.disabled,
    'is-focused': isFocused.value,
    'has-error': props.errorMessage || props.validateStatus === 'error',
    'has-prefix': props.prefixIcon || props.$slots.prefix,
    'has-suffix': props.suffixIcon || props.$slots.suffix || showClearIcon.value || showPasswordIcon.value
  }
])

// 输入框类名
const inputClasses = computed(() => [
  'svg-input',
  {
    'has-prefix': props.prefixIcon || props.$slots.prefix,
    'has-suffix': props.suffixIcon || props.$slots.suffix || showClearIcon.value || showPasswordIcon.value
  }
])

// 事件处理
const handleFocus = (event) => {
  isFocused.value = true
  emits('focus', event)
}

const handleBlur = (event) => {
  isFocused.value = false
  emits('blur', event)
}

const handleInput = (event) => {
  emits('input', event)
}

const handleChange = (event) => {
  emits('change', event)
}

const handleKeydown = (event) => {
  emits('keydown', event)
}

const handleClear = () => {
  inputValue.value = ''
  emits('clear')
  inputRef.value?.focus()
}

const togglePasswordVisibility = () => {
  showPasswordVisible.value = !showPasswordVisible.value
}

// 公开方法
const focus = () => {
  inputRef.value?.focus()
}

const blur = () => {
  inputRef.value?.blur()
}

const select = () => {
  inputRef.value?.select()
}

defineExpose({
  focus,
  blur,
  select,
  inputRef
})
</script>

<style scoped>
.svg-input-container {
  position: relative;
  display: inline-block;
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.input-border {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.focus-ring {
  animation: focusRing 0.3s ease-out;
}

@keyframes focusRing {
  from {
    opacity: 0;
    transform: scale(1.05);
  }
  to {
    opacity: 0.6;
    transform: scale(1);
  }
}

.svg-input {
  position: relative;
  z-index: 2;
  width: 100%;
  height: v-bind('sizeConfig.height + "px"');
  padding: 0 v-bind('sizeConfig.padding + "px"');
  border: none;
  background: transparent;
  font-size: v-bind('sizeConfig.fontSize + "px"');
  color: #1d1d1f;
  outline: none;
  box-sizing: border-box;
  font-family: inherit;
}

.svg-input.has-prefix {
  padding-left: v-bind('(sizeConfig.padding + sizeConfig.iconSize + 8) + "px"');
}

.svg-input.has-suffix {
  padding-right: v-bind('(sizeConfig.padding + sizeConfig.iconSize + 8) + "px"');
}

.svg-input::placeholder {
  color: #8e8e93;
  font-weight: 400;
}

.svg-input:disabled {
  color: #8e8e93;
  cursor: not-allowed;
}

.input-prefix,
.input-suffix {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #8e8e93;
}

.input-prefix {
  left: v-bind('sizeConfig.padding + "px"');
}

.input-suffix {
  right: v-bind('sizeConfig.padding + "px"');
}

.clear-btn,
.password-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  padding: 2px;
  border-radius: 50%;
  transition: all 0.2s ease;
  color: #8e8e93;
}

.clear-btn:hover,
.password-btn:hover {
  transform: scale(1.1);
  color: #636366;
}

.error-message {
  margin-top: 6px;
  font-size: 12px;
  color: #ff3b30;
  font-weight: 400;
}

.help-text {
  margin-top: 6px;
  font-size: 12px;
  color: #8e8e93;
  font-weight: 400;
}

/* 尺寸变体 */
.svg-input--small .svg-input {
  font-size: 13px;
}

.svg-input--large .svg-input {
  font-size: 16px;
}

/* 状态样式 */
.svg-input-container.is-disabled {
  opacity: 0.6;
}

.svg-input-container.has-error .error-message {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-2px); }
  75% { transform: translateX(2px); }
}
</style>