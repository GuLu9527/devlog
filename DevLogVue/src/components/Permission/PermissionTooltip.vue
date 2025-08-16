<!--
  权限提示组件
  为按钮和操作元素提供权限相关的工具提示
-->

<template>
  <el-tooltip
    :content="tooltipContent"
    :placement="placement"
    :disabled="!showTooltip"
    :show-after="delay"
    :popper-class="tooltipClass"
  >
    <span 
      :class="wrapperClass"
      @click="handleClick"
    >
      <slot />
    </span>
  </el-tooltip>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 需要的权限
  permission: {
    type: [String, Array],
    default: null
  },
  // 需要的角色
  role: {
    type: [String, Array],
    default: null
  },
  // 是否需要全部权限
  requireAll: {
    type: Boolean,
    default: false
  },
  // 工具提示位置
  placement: {
    type: String,
    default: 'top'
  },
  // 显示延迟
  delay: {
    type: Number,
    default: 500
  },
  // 有权限时的提示文本
  allowedText: {
    type: String,
    default: ''
  },
  // 无权限时的提示文本
  deniedText: {
    type: String,
    default: ''
  },
  // 是否在无权限时显示警告消息
  showWarningMessage: {
    type: Boolean,
    default: true
  },
  // 是否禁用无权限的元素
  disableWhenDenied: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['permission-denied', 'permission-granted'])

const authStore = useAuthStore()

// 权限检查
const hasPermission = computed(() => {
  if (!authStore.hasInitialized) {
    return false
  }
  
  let hasAccess = true
  
  // 角色检查
  if (props.role) {
    const hasRole = Array.isArray(props.role) 
      ? props.role.some(r => authStore.hasRole(r))
      : authStore.hasRole(props.role)
    
    if (!hasRole) {
      hasAccess = false
    }
  }
  
  // 权限检查
  if (props.permission && hasAccess) {
    if (Array.isArray(props.permission)) {
      if (props.requireAll) {
        hasAccess = authStore.hasAllPermissions(props.permission)
      } else {
        hasAccess = authStore.hasPermission(props.permission)
      }
    } else {
      hasAccess = authStore.hasPermission(props.permission)
    }
  }
  
  return hasAccess
})

// 是否显示工具提示
const showTooltip = computed(() => {
  return props.allowedText || !hasPermission.value
})

// 工具提示内容
const tooltipContent = computed(() => {
  if (hasPermission.value) {
    return props.allowedText || ''
  }
  
  if (props.deniedText) {
    return props.deniedText
  }
  
  // 生成默认的权限缺失提示
  let message = '权限不足，无法执行此操作'
  
  if (props.role && !props.permission) {
    const roleText = Array.isArray(props.role) 
      ? props.role.join(' 或 ') 
      : props.role
    message = `需要 ${roleText} 角色权限`
  } else if (props.permission && !props.role) {
    if (Array.isArray(props.permission)) {
      const permText = props.requireAll ? '全部权限' : '任一权限'
      message = `需要 ${permText}：${props.permission.join(', ')}`
    } else {
      message = `需要权限：${props.permission}`
    }
  } else if (props.role && props.permission) {
    message = '角色和权限都不满足要求'
  }
  
  return message
})

// 工具提示样式类
const tooltipClass = computed(() => {
  return hasPermission.value 
    ? 'permission-tooltip-allowed'
    : 'permission-tooltip-denied'
})

// 包装器样式类
const wrapperClass = computed(() => {
  const classes = ['permission-tooltip-wrapper']
  
  if (!hasPermission.value && props.disableWhenDenied) {
    classes.push('permission-disabled')
  }
  
  return classes
})

// 处理点击事件
const handleClick = (event) => {
  if (!hasPermission.value) {
    // 阻止事件传播
    event.preventDefault()
    event.stopPropagation()
    
    // 触发权限拒绝事件
    emit('permission-denied', {
      permission: props.permission,
      role: props.role,
      message: tooltipContent.value
    })
    
    // 显示警告消息
    if (props.showWarningMessage) {
      ElMessage.warning(tooltipContent.value)
    }
  } else {
    // 触发权限通过事件
    emit('permission-granted', {
      permission: props.permission,
      role: props.role
    })
  }
}
</script>

<style>
/* 工具提示样式 */
.permission-tooltip-allowed {
  .el-tooltip__popper {
    background: #34C759 !important;
    border-color: #34C759 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="top"] .el-popper__arrow::before {
    border-top-color: #34C759 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="bottom"] .el-popper__arrow::before {
    border-bottom-color: #34C759 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="left"] .el-popper__arrow::before {
    border-left-color: #34C759 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="right"] .el-popper__arrow::before {
    border-right-color: #34C759 !important;
  }
}

.permission-tooltip-denied {
  .el-tooltip__popper {
    background: #FF3B30 !important;
    border-color: #FF3B30 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="top"] .el-popper__arrow::before {
    border-top-color: #FF3B30 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="bottom"] .el-popper__arrow::before {
    border-bottom-color: #FF3B30 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="left"] .el-popper__arrow::before {
    border-left-color: #FF3B30 !important;
  }
  
  .el-tooltip__popper[data-popper-placement^="right"] .el-popper__arrow::before {
    border-right-color: #FF3B30 !important;
  }
}
</style>

<style scoped>
.permission-tooltip-wrapper {
  display: inline-block;
  width: 100%;
  height: 100%;
}

.permission-disabled {
  cursor: not-allowed;
  opacity: 0.6;
  
  :deep(*) {
    pointer-events: none;
  }
}

.permission-disabled:hover {
  opacity: 0.8;
}
</style>