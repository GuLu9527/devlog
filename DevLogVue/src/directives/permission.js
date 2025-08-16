/**
 * 权限指令
 * 用于控制页面元素的显示/隐藏
 */

import { useAuthStore } from '@/stores/auth'

/**
 * v-permission 指令
 * 
 * 使用方式：
 * 1. 单个权限：v-permission="'user:create'"
 * 2. 多个权限（任一满足）：v-permission="['user:create', 'user:update']"
 * 3. 多个权限（全部满足）：v-permission.all="['user:create', 'user:update']"
 * 4. 角色检查：v-permission.role="'ADMIN'"
 * 5. 禁用元素而非隐藏：v-permission.disable="'user:create'"
 */
export const permission = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

/**
 * v-role 指令
 * 专门用于角色检查
 * 
 * 使用方式：
 * 1. 单个角色：v-role="'ADMIN'"
 * 2. 多个角色：v-role="['ADMIN', 'MANAGER']"
 */
export const role = {
  mounted(el, binding) {
    checkRole(el, binding)
  },
  updated(el, binding) {
    checkRole(el, binding)
  }
}

/**
 * 检查权限并处理元素
 */
function checkPermission(el, binding) {
  const { value, modifiers } = binding
  const authStore = useAuthStore()
  
  if (!value) {
    console.warn('v-permission 指令需要提供权限值')
    return
  }
  
  let hasPermission = false
  
  // 角色检查模式
  if (modifiers.role) {
    hasPermission = Array.isArray(value) 
      ? value.some(role => authStore.hasRole(role))
      : authStore.hasRole(value)
  } else {
    // 权限检查模式
    if (modifiers.all) {
      // 需要全部权限
      hasPermission = Array.isArray(value) 
        ? authStore.hasAllPermissions(value)
        : authStore.hasPermission(value)
    } else {
      // 任一权限即可（默认）
      hasPermission = authStore.hasPermission(value)
    }
  }
  
  // 处理元素
  handleElement(el, hasPermission, modifiers)
}

/**
 * 检查角色并处理元素
 */
function checkRole(el, binding) {
  const { value, modifiers } = binding
  const authStore = useAuthStore()
  
  if (!value) {
    console.warn('v-role 指令需要提供角色值')
    return
  }
  
  const hasRole = Array.isArray(value) 
    ? value.some(role => authStore.hasRole(role))
    : authStore.hasRole(value)
  
  handleElement(el, hasRole, modifiers)
}

/**
 * 处理元素的显示/隐藏/禁用
 */
function handleElement(el, hasPermission, modifiers) {
  if (modifiers.disable) {
    // 禁用模式：没有权限时禁用元素
    el.disabled = !hasPermission
    if (!hasPermission) {
      el.classList.add('is-disabled')
      el.style.opacity = '0.5'
      el.style.cursor = 'not-allowed'
      el.title = '您没有操作权限'
    } else {
      el.classList.remove('is-disabled')
      el.style.opacity = ''
      el.style.cursor = ''
      el.title = ''
    }
  } else {
    // 默认模式：没有权限时隐藏元素
    if (!hasPermission) {
      // 保存原始显示状态
      if (!el._originalDisplay) {
        el._originalDisplay = el.style.display || ''
      }
      el.style.display = 'none'
    } else {
      // 恢复显示
      el.style.display = el._originalDisplay || ''
    }
  }
}

/**
 * 权限指令插件
 */
export default {
  install(app) {
    app.directive('permission', permission)
    app.directive('role', role)
  }
}