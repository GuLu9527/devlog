<template>
  <svg 
    :width="size" 
    :height="size" 
    :class="['status-icon', animationClass]"
    viewBox="0 0 24 24"
  >
    <!-- 待审核状态 -->
    <g v-if="status === 'pending'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#8892b0" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.6"
      />
      <path 
        d="M12 6v6l4 2" 
        stroke="#64748b" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        fill="none"
        opacity="0.8"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="1.5" 
        fill="#64748b"
        opacity="0.7"
      />
    </g>
    
    <!-- 已通过状态 -->
    <g v-else-if="status === 'approved'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#64748b" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.5"
      />
      <path 
        d="m9 12 2 2 4-4" 
        stroke="#475569" 
        stroke-width="2" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.8"
      />
    </g>
    
    <!-- 已拒绝状态 -->
    <g v-else-if="status === 'rejected'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#94a3b8" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.5"
      />
      <path 
        d="m15 9-6 6M9 9l6 6" 
        stroke="#64748b" 
        stroke-width="2" 
        stroke-linecap="round" 
        fill="none"
        opacity="0.7"
      />
    </g>
    
    <!-- 默认状态 -->
    <g v-else>
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#cbd5e1" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.4"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="2" 
        fill="#cbd5e1"
        opacity="0.6"
      />
    </g>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    default: 'default',
    validator: (value) => ['pending', 'approved', 'rejected', 'default'].includes(value)
  },
  size: {
    type: [String, Number],
    default: 24
  },
  animate: {
    type: Boolean,
    default: false
  }
})

const animationClass = computed(() => {
  if (!props.animate) return ''
  return props.status === 'pending' ? 'pulse-animation' : 'fade-in-animation'
})
</script>

<style scoped>
.status-icon {
  transition: all 0.3s ease;
}

.status-icon:hover {
  transform: scale(1.1);
}

.pulse-animation {
  animation: pulse 2s infinite;
}

.fade-in-animation {
  animation: fadeIn 0.5s ease-in;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>