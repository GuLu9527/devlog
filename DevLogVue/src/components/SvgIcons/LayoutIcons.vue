<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    :class="['layout-icon', `icon-${type}`]"
  >
    <!-- 首页图标 -->
    <g v-if="type === 'home'">
      <path 
        d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <polyline 
        points="9,22 9,12 15,12 15,22" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 用户管理图标 -->
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
    
    <!-- 角色管理图标 -->
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
    
    <!-- 权限管理图标 -->
    <g v-else-if="type === 'permission'">
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
    </g>
    
    <!-- 项目管理图标 -->
    <g v-else-if="type === 'project'">
      <path 
        d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <line 
        x1="12" 
        y1="11" 
        x2="12" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
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
    
    <!-- 任务管理图标 -->
    <g v-else-if="type === 'task'">
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
        opacity="0.4"
      />
    </g>
    
    <!-- 部门管理图标 -->
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
    
    <!-- 小组管理图标 -->
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
    
    <!-- 工作日志图标 -->
    <g v-else-if="type === 'worklog'">
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
        opacity="0.7"
      />
      <line 
        x1="16" 
        y1="13" 
        x2="8" 
        y2="13" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <line 
        x1="16" 
        y1="17" 
        x2="8" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <polyline 
        points="10,9 9,9 8,9" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.4"
      />
    </g>
    
    <!-- 通知消息图标 -->
    <g v-else-if="type === 'notification'">
      <path 
        d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <path 
        d="M13.73 21a2 2 0 0 1-3.46 0" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 菜单图标 -->
    <g v-else-if="type === 'menu'">
      <line 
        x1="3" 
        y1="6" 
        x2="21" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="3" 
        y1="12" 
        x2="21" 
        y2="12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="3" 
        y1="18" 
        x2="21" 
        y2="18" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 设置图标 -->
    <g v-else-if="type === 'setting'">
      <circle 
        cx="12" 
        cy="12" 
        r="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.8"
      />
      <path 
        d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
    </g>
    
    <!-- 退出登录图标 -->
    <g v-else-if="type === 'logout'">
      <path 
        d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <polyline 
        points="16,17 21,12 16,7" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <line 
        x1="21" 
        y1="12" 
        x2="9" 
        y2="12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 仪表板图标 -->
    <g v-else-if="type === 'dashboard'">
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
        x="14" 
        y="14" 
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
    </g>
    
    <!-- 依赖链接图标 -->
    <g v-else-if="type === 'link'">
      <path 
        d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <path 
        d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 时间线图标 -->
    <g v-else-if="type === 'timeline'">
      <line 
        x1="3" 
        y1="6" 
        x2="21" 
        y2="6" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.6"
      />
      <line 
        x1="3" 
        y1="12" 
        x2="21" 
        y2="12" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.6"
      />
      <line 
        x1="3" 
        y1="18" 
        x2="21" 
        y2="18" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.6"
      />
      <circle 
        cx="8" 
        cy="6" 
        r="2" 
        fill="currentColor" 
        opacity="0.8"
      />
      <circle 
        cx="16" 
        cy="12" 
        r="2" 
        fill="currentColor" 
        opacity="0.8"
      />
      <circle 
        cx="12" 
        cy="18" 
        r="2" 
        fill="currentColor" 
        opacity="0.8"
      />
    </g>
    
    <!-- 模板图标 -->
    <g v-else-if="type === 'template'">
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
        opacity="0.7"
      />
      <line 
        x1="16" 
        y1="13" 
        x2="8" 
        y2="13" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <line 
        x1="16" 
        y1="17" 
        x2="8" 
        y2="17" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.4"
      />
      <circle 
        cx="10" 
        cy="15" 
        r="1" 
        fill="currentColor" 
        opacity="0.6"
      />
    </g>
    
    <!-- 工时统计图标 -->
    <g v-else-if="type === 'clock'">
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
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="1" 
        fill="currentColor" 
        opacity="0.9"
      />
    </g>
    
    <!-- 分析图标 -->
    <g v-else-if="type === 'analytics'">
      <path 
        d="M3 3v18h18" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <path 
        d="M18 9l-5 5-4-4-4 4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <circle 
        cx="18" 
        cy="9" 
        r="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <circle 
        cx="13" 
        cy="14" 
        r="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <circle 
        cx="9" 
        cy="10" 
        r="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
      <circle 
        cx="5" 
        cy="14" 
        r="2" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.7"
      />
    </g>
    
    <!-- 标签图标 -->
    <g v-else-if="type === 'tag'">
      <path 
        d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        fill="none"
        opacity="0.7"
      />
      <circle 
        cx="7" 
        cy="7" 
        r="1.5" 
        fill="currentColor"
        opacity="0.8"
      />
    </g>

    <!-- 下拉箭头 -->
    <g v-else-if="type === 'chevron-down'">
      <polyline 
        points="6,9 12,15 18,9" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
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
      'home', 'user', 'role', 'permission', 'project', 'task', 
      'department', 'group', 'worklog', 'notification', 'menu',
      'setting', 'logout', 'chevron-down', 'dashboard', 'link',
      'timeline', 'template', 'clock', 'analytics', 'tag'
    ].includes(value)
  },
  size: {
    type: [String, Number],
    default: 20
  }
})
</script>

<style scoped>
.layout-icon {
  color: #64748b;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.layout-icon:hover {
  color: #475569;
}

/* 不同类型的图标可以有特定的颜色 */
.icon-home { color: #64748b; }
.icon-user { color: #64748b; }
.icon-role { color: #64748b; }
.icon-permission { color: #64748b; }
.icon-project { color: #64748b; }
.icon-task { color: #64748b; }
.icon-department { color: #64748b; }
.icon-group { color: #64748b; }
.icon-worklog { color: #64748b; }
.icon-notification { color: #64748b; }
.icon-menu { color: #64748b; }
.icon-setting { color: #64748b; }
.icon-logout { color: #64748b; }
.icon-chevron-down { color: #64748b; }
</style>