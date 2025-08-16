<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    :class="['dashboard-icon', `icon-${type}`]"
  >
    <!-- 时钟图标 -->
    <g v-if="type === 'clock'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <polyline 
        points="12,6 12,12 16,14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 文档图标 -->
    <g v-else-if="type === 'document'">
      <path 
        d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <polyline 
        points="14,2 14,8 20,8" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <line 
        x1="16" 
        y1="13" 
        x2="8" 
        y2="13" 
        stroke="currentColor" 
        stroke-width="1" 
        opacity="0.5"
      />
      <line 
        x1="16" 
        y1="17" 
        x2="8" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1" 
        opacity="0.5"
      />
    </g>
    
    <!-- 文件夹图标 -->
    <g v-else-if="type === 'folder'">
      <path 
        d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
    </g>
    
    <!-- 用户图标 -->
    <g v-else-if="type === 'user'">
      <path 
        d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <circle 
        cx="12" 
        cy="7" 
        r="4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.8"
      />
    </g>
    
    <!-- 趋势图标 -->
    <g v-else-if="type === 'trend'">
      <polyline 
        points="22,12 18,12 15,21 9,3 6,12 2,12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 仪表板图标 -->
    <g v-else-if="type === 'dashboard'">
      <rect 
        x="3" 
        y="3" 
        width="7" 
        height="7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        rx="1" 
        opacity="0.6"
      />
      <rect 
        x="14" 
        y="3" 
        width="7" 
        height="7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        rx="1" 
        opacity="0.6"
      />
      <rect 
        x="14" 
        y="14" 
        width="7" 
        height="7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        rx="1" 
        opacity="0.6"
      />
      <rect 
        x="3" 
        y="14" 
        width="7" 
        height="7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        rx="1" 
        opacity="0.6"
      />
    </g>
    
    <!-- 日历图标 -->
    <g v-else-if="type === 'calendar'">
      <rect 
        x="3" 
        y="4" 
        width="18" 
        height="18" 
        rx="2" 
        ry="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <line 
        x1="16" 
        y1="2" 
        x2="16" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="8" 
        y1="2" 
        x2="8" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="3" 
        y1="10" 
        x2="21" 
        y2="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.5"
      />
    </g>
    
    <!-- 计时器图标 -->
    <g v-else-if="type === 'timer'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <polyline 
        points="12,6 12,12 8,14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <path 
        d="M9 1h6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.5"
      />
    </g>
    
    <!-- 刷新图标 -->
    <g v-else-if="type === 'refresh'">
      <polyline 
        points="23 4 23 10 17 10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <polyline 
        points="1 20 1 14 7 14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <path 
        d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4-4.64 4.36A9 9 0 0 1 3.51 15" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
    </g>
  </svg>
</template>

<script setup>
const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => [
      'clock', 'document', 'folder', 'user', 'trend', 
      'dashboard', 'calendar', 'timer', 'refresh'
    ].includes(value)
  },
  size: {
    type: [String, Number],
    default: 24
  }
})
</script>

<style scoped>
.dashboard-icon {
  color: #64748b;
  transition: all 0.3s ease;
}

.dashboard-icon:hover {
  color: #475569;
  transform: scale(1.1);
}

.icon-clock { color: #64748b; }
.icon-document { color: #64748b; }
.icon-folder { color: #64748b; }
.icon-user { color: #64748b; }
.icon-trend { color: #64748b; }
.icon-dashboard { color: #64748b; }
.icon-calendar { color: #64748b; }
.icon-timer { color: #64748b; }
.icon-refresh { color: #64748b; }
</style>