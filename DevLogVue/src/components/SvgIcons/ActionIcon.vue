<template>
  <svg 
    :width="size" 
    :height="size" 
    :class="['action-icon', hoverClass]"
    viewBox="0 0 24 24"
    @click="handleClick"
  >
    <!-- 查看图标 -->
    <g v-if="action === 'view'">
      <path 
        d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" 
        stroke="#64748b" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.7"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="3" 
        stroke="#475569" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.8"
      />
    </g>
    
    <!-- 编辑图标 -->
    <g v-else-if="action === 'edit'">
      <path 
        d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" 
        stroke="#64748b" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
      <path 
        d="m18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" 
        stroke="#475569" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.8"
      />
    </g>
    
    <!-- 删除图标 -->
    <g v-else-if="action === 'delete'">
      <path 
        d="m3 6 18 0M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" 
        stroke="#94a3b8" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.6"
      />
      <line 
        x1="10" 
        y1="11" 
        x2="10" 
        y2="17" 
        stroke="#64748b" 
        stroke-width="1.5" 
        stroke-linecap="round"
        opacity="0.7"
      />
      <line 
        x1="14" 
        y1="11" 
        x2="14" 
        y2="17" 
        stroke="#64748b" 
        stroke-width="1.5" 
        stroke-linecap="round"
        opacity="0.7"
      />
    </g>
    
    <!-- 通过图标 -->
    <g v-else-if="action === 'approve'">
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
    
    <!-- 拒绝图标 -->
    <g v-else-if="action === 'reject'">
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
        stroke-width="1.5" 
        stroke-linecap="round" 
        fill="none"
        opacity="0.7"
      />
    </g>
    
    <!-- 导出图标 -->
    <g v-else-if="action === 'export'">
      <path 
        d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5-5 5 5M12 15V5" 
        stroke="#64748b" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
    </g>
    
    <!-- 添加图标 -->
    <g v-else-if="action === 'add'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="#64748b" 
        stroke-width="1.5" 
        fill="none"
        opacity="0.6"
      />
      <path 
        d="M12 8v8M8 12h8" 
        stroke="#475569" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        fill="none"
        opacity="0.8"
      />
    </g>
    
    <!-- 通知图标 -->
    <g v-else-if="action === 'notification'">
      <path 
        d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
      <path 
        d="M13.73 21a2 2 0 0 1-3.46 0" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.6"
      />
    </g>
    
    <!-- 搜索图标 -->
    <g v-else-if="action === 'search'">
      <circle 
        cx="11" 
        cy="11" 
        r="8" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <path 
        d="m21 21-4.35-4.35" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 刷新图标 -->
    <g v-else-if="action === 'refresh'">
      <path 
        d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <path 
        d="M21 3v5h-5" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <path 
        d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <path 
        d="M3 21v-5h5" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
    </g>
    
    <!-- 上传图标 -->
    <g v-else-if="action === 'upload'">
      <path 
        d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
      <path 
        d="M7 10l5-5 5 5" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.8"
      />
      <path 
        d="M12 15V5" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        fill="none"
        opacity="0.8"
      />
    </g>
    
    <!-- 关闭图标 -->
    <g v-else-if="action === 'close'">
      <path 
        d="M18 6L6 18M6 6l12 12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
    </g>
    
    <!-- 确认图标 -->
    <g v-else-if="action === 'check'">
      <path 
        d="M20 6L9 17l-5-5" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.8"
      />
    </g>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  action: {
    type: String,
    required: true,
    validator: (value) => ['view', 'edit', 'delete', 'approve', 'reject', 'export', 'add', 'notification', 'search', 'refresh', 'upload', 'close', 'check'].includes(value)
  },
  size: {
    type: [String, Number],
    default: 20
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const hoverClass = computed(() => {
  return props.disabled ? 'disabled' : 'hoverable'
})

const handleClick = (event) => {
  if (!props.disabled) {
    emit('click', event)
  }
}
</script>

<style scoped>
.action-icon {
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-icon.hoverable:hover {
  transform: scale(1.2);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.action-icon.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-icon.disabled:hover {
  transform: none;
  filter: none;
}
</style>