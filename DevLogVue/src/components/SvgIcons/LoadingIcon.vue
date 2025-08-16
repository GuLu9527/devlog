<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    class="loading-icon"
  >
    <!-- 圆环加载动画 -->
    <g v-if="type === 'spinner'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#e6f7ff" 
        stroke-width="2" 
        fill="none"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#1890ff" 
        stroke-width="2" 
        stroke-linecap="round" 
        fill="none"
        stroke-dasharray="62.83"
        stroke-dashoffset="62.83"
        class="spinner-path"
      />
    </g>
    
    <!-- 脉冲动画 -->
    <g v-else-if="type === 'pulse'">
      <circle 
        cx="12" 
        cy="12" 
        r="8" 
        fill="#1890ff" 
        opacity="0.3"
        class="pulse-outer"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="5" 
        fill="#1890ff" 
        class="pulse-inner"
      />
    </g>
    
    <!-- 点跳动画 -->
    <g v-else-if="type === 'dots'">
      <circle cx="6" cy="12" r="2" fill="#1890ff" class="dot-1"/>
      <circle cx="12" cy="12" r="2" fill="#1890ff" class="dot-2"/>
      <circle cx="18" cy="12" r="2" fill="#1890ff" class="dot-3"/>
    </g>
    
    <!-- 波浪动画 -->
    <g v-else-if="type === 'wave'">
      <rect x="4" y="10" width="2" height="4" fill="#1890ff" class="wave-1"/>
      <rect x="8" y="8" width="2" height="8" fill="#1890ff" class="wave-2"/>
      <rect x="12" y="6" width="2" height="12" fill="#1890ff" class="wave-3"/>
      <rect x="16" y="8" width="2" height="8" fill="#1890ff" class="wave-4"/>
      <rect x="20" y="10" width="2" height="4" fill="#1890ff" class="wave-5"/>
    </g>
  </svg>
</template>

<script setup>
const props = defineProps({
  type: {
    type: String,
    default: 'spinner',
    validator: (value) => ['spinner', 'pulse', 'dots', 'wave'].includes(value)
  },
  size: {
    type: [String, Number],
    default: 24
  }
})
</script>

<style scoped>
.loading-icon {
  display: inline-block;
}

/* 圆环旋转动画 */
.spinner-path {
  animation: spin 1.5s linear infinite, dash 1.5s ease-in-out infinite;
  transform-origin: center;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1 62.83;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 31.42 31.42;
    stroke-dashoffset: -7.85;
  }
  100% {
    stroke-dasharray: 1 62.83;
    stroke-dashoffset: -62.83;
  }
}

/* 脉冲动画 */
.pulse-outer {
  animation: pulseOuter 2s ease-in-out infinite;
}

.pulse-inner {
  animation: pulseInner 2s ease-in-out infinite;
}

@keyframes pulseOuter {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.1;
    transform: scale(1.2);
  }
}

@keyframes pulseInner {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(0.8);
  }
}

/* 点跳动画 */
.dot-1 {
  animation: dotBounce 1.4s ease-in-out infinite both;
  animation-delay: -0.32s;
}

.dot-2 {
  animation: dotBounce 1.4s ease-in-out infinite both;
  animation-delay: -0.16s;
}

.dot-3 {
  animation: dotBounce 1.4s ease-in-out infinite both;
}

@keyframes dotBounce {
  0%, 80%, 100% {
    transform: scale(0.7);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 波浪动画 */
.wave-1 {
  animation: waveScale 1.2s ease-in-out infinite;
  animation-delay: 0s;
}

.wave-2 {
  animation: waveScale 1.2s ease-in-out infinite;
  animation-delay: 0.1s;
}

.wave-3 {
  animation: waveScale 1.2s ease-in-out infinite;
  animation-delay: 0.2s;
}

.wave-4 {
  animation: waveScale 1.2s ease-in-out infinite;
  animation-delay: 0.3s;
}

.wave-5 {
  animation: waveScale 1.2s ease-in-out infinite;
  animation-delay: 0.4s;
}

@keyframes waveScale {
  0%, 40%, 100% {
    transform: scaleY(0.4);
  }
  20% {
    transform: scaleY(1);
  }
}
</style>