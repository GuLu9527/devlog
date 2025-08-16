<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    :class="['user-icon', `icon-${type}`]"
  >
    <!-- 搜索图标 -->
    <g v-if="type === 'search'">
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
    
    <!-- 重置图标 -->
    <g v-else-if="type === 'refresh'">
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
    
    <!-- 新增用户图标 -->
    <g v-else-if="type === 'add-user'">
      <path 
        d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <circle 
        cx="9" 
        cy="7" 
        r="4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <line 
        x1="19" 
        y1="8" 
        x2="19" 
        y2="14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="22" 
        y1="11" 
        x2="16" 
        y2="11" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 编辑图标 -->
    <g v-else-if="type === 'edit'">
      <path 
        d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <path 
        d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 重置密码图标 -->
    <g v-else-if="type === 'reset-password'">
      <rect 
        x="3" 
        y="11" 
        width="18" 
        height="10" 
        rx="2" 
        ry="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <circle 
        cx="12" 
        cy="16" 
        r="1" 
        fill="currentColor" 
        opacity="0.8"
      />
      <path 
        d="M7 11V7a5 5 0 1 1 10 0v4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <line 
        x1="9" 
        y1="14" 
        x2="15" 
        y2="14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
    </g>
    
    <!-- 删除图标 -->
    <g v-else-if="type === 'delete'">
      <polyline 
        points="3,6 5,6 21,6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <path 
        d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <line 
        x1="10" 
        y1="11" 
        x2="10" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.5"
      />
      <line 
        x1="14" 
        y1="11" 
        x2="14" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.5"
      />
    </g>
    
    <!-- 用户状态图标 -->
    <g v-else-if="type === 'status'">
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <polyline 
        points="9,12 11,14 15,10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 部门图标 -->
    <g v-else-if="type === 'department'">
      <rect 
        x="4" 
        y="2" 
        width="16" 
        height="20" 
        rx="2" 
        ry="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
      <line 
        x1="9" 
        y1="9" 
        x2="15" 
        y2="9" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <line 
        x1="9" 
        y1="13" 
        x2="15" 
        y2="13" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <line 
        x1="9" 
        y1="17" 
        x2="13" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
    </g>
    
    <!-- 角色图标 -->
    <g v-else-if="type === 'role'">
      <path 
        d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <circle 
        cx="9" 
        cy="7" 
        r="4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <path 
        d="M23 21v-2a4 4 0 0 0-3-3.87" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.5"
      />
      <path 
        d="M16 3.13a4 4 0 0 1 0 7.75" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.5"
      />
    </g>
    
    <!-- 小组图标 -->
    <g v-else-if="type === 'group'">
      <path 
        d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <circle 
        cx="9" 
        cy="7" 
        r="4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <path 
        d="M23 21v-2a4 4 0 0 0-3-3.87" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.5"
      />
      <path 
        d="M16 3.13a4 4 0 0 1 0 7.75" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.5"
      />
      <circle 
        cx="18" 
        cy="18" 
        r="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.8"
      />
    </g>
    
    <!-- 卡片视图图标 -->
    <g v-else-if="type === 'card-view'">
      <rect 
        x="3" 
        y="3" 
        width="7" 
        height="7" 
        rx="1" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <rect 
        x="14" 
        y="3" 
        width="7" 
        height="7" 
        rx="1" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <rect 
        x="3" 
        y="14" 
        width="7" 
        height="7" 
        rx="1" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <rect 
        x="14" 
        y="14" 
        width="7" 
        height="7" 
        rx="1" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
    </g>
    
    <!-- 列表视图图标 -->
    <g v-else-if="type === 'list-view'">
      <line 
        x1="8" 
        y1="6" 
        x2="21" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="8" 
        y1="12" 
        x2="21" 
        y2="12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="8" 
        y1="18" 
        x2="21" 
        y2="18" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="3" 
        y1="6" 
        x2="3.01" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="2" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="3" 
        y1="12" 
        x2="3.01" 
        y2="12" 
        stroke="currentColor" 
        stroke-width="2" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="3" 
        y1="18" 
        x2="3.01" 
        y2="18" 
        stroke="currentColor" 
        stroke-width="2" 
        stroke-linecap="round" 
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
      'search', 'refresh', 'add-user', 'edit', 'reset-password', 'delete',
      'status', 'department', 'role', 'group', 'card-view', 'list-view'
    ].includes(value)
  },
  size: {
    type: [String, Number],
    default: 20
  }
})
</script>

<style scoped>
.user-icon {
  color: #64748b;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.user-icon:hover {
  color: #475569;
}

/* 不同类型的图标可以有特定的颜色 */
.icon-search { color: #64748b; }
.icon-refresh { color: #64748b; }
.icon-add-user { color: #475569; }
.icon-edit { color: #64748b; }
.icon-reset-password { color: #64748b; }
.icon-delete { color: #ef4444; opacity: 0.7; }
.icon-status { color: #64748b; }
.icon-department { color: #64748b; }
.icon-role { color: #64748b; }
.icon-group { color: #64748b; }
.icon-card-view { color: #64748b; }
.icon-list-view { color: #64748b; }

.icon-delete:hover {
  color: #dc2626;
  opacity: 0.9;
}
</style>