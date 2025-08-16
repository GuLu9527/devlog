<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    :class="['skill-icon', `icon-${type}`]"
  >
    <!-- 标签图标 -->
    <g v-if="type === 'tag'">
      <path 
        d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 技能等级图标 -->
    <g v-else-if="type === 'level'">
      <polygon 
        points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.5"
      />
    </g>
    
    <!-- 经验图标 -->
    <g v-else-if="type === 'experience'">
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
    </g>
    
    <!-- 匹配度图标 -->
    <g v-else-if="type === 'match'">
      <path 
        d="M9 12l2 2 4-4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <circle 
        cx="12" 
        cy="12" 
        r="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.6"
      />
    </g>
    
    <!-- 技能图标 -->
    <g v-else-if="type === 'skill'">
      <path 
        d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 分类图标 -->
    <g v-else-if="type === 'category'">
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
    
    <!-- 推荐图标 -->
    <g v-else-if="type === 'recommend'">
      <path 
        d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <circle 
        cx="18" 
        cy="6" 
        r="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        opacity="0.8"
      />
      <line 
        x1="18" 
        y1="4" 
        x2="18" 
        y2="8" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
    </g>
    
    <!-- 统计图标 -->
    <g v-else-if="type === 'statistics'">
      <line 
        x1="18" 
        y1="20" 
        x2="18" 
        y2="10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
      <line 
        x1="12" 
        y1="20" 
        x2="12" 
        y2="4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.8"
      />
      <line 
        x1="6" 
        y1="20" 
        x2="6" 
        y2="14" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.6"
      />
    </g>
    
    <!-- 需求图标 -->
    <g v-else-if="type === 'requirement'">
      <path 
        d="M21 15.999v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <polyline 
        points="7,10 12,15 17,10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <line 
        x1="12" 
        y1="15" 
        x2="12" 
        y2="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 导入图标 -->
    <g v-else-if="type === 'import'">
      <path 
        d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <polyline 
        points="17,8 12,3 7,8" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <line 
        x1="12" 
        y1="3" 
        x2="12" 
        y2="15" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 导出图标 -->
    <g v-else-if="type === 'export'">
      <path 
        d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.6"
      />
      <polyline 
        points="7,10 12,15 17,10" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.8"
      />
      <line 
        x1="12" 
        y1="15" 
        x2="12" 
        y2="3" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        opacity="0.7"
      />
    </g>
    
    <!-- 热度图标 -->
    <g v-else-if="type === 'trending'">
      <polyline 
        points="23,6 13.5,15.5 8.5,10.5 1,18" 
        stroke="currentColor" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-linejoin="round" 
        opacity="0.7"
      />
      <polyline 
        points="17,6 23,6 23,12" 
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
      'tag', 'level', 'experience', 'match', 'skill', 'category',
      'recommend', 'statistics', 'requirement', 'import', 'export', 'trending'
    ].includes(value)
  },
  size: {
    type: [String, Number],
    default: 20
  }
})
</script>

<style scoped>
.skill-icon {
  color: #64748b;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.skill-icon:hover {
  color: #0ea5e9;
}

/* 不同类型的图标可以有特定的颜色 */
.icon-tag { color: #0ea5e9; }
.icon-level { color: #f59e0b; }
.icon-experience { color: #10b981; }
.icon-match { color: #8b5cf6; }
.icon-skill { color: #06b6d4; }
.icon-category { color: #64748b; }
.icon-recommend { color: #f59e0b; }
.icon-statistics { color: #6366f1; }
.icon-requirement { color: #ef4444; }
.icon-import { color: #10b981; }
.icon-export { color: #f59e0b; }
.icon-trending { color: #ec4899; }
</style>