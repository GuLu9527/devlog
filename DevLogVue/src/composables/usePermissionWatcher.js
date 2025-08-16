/**
 * 权限变更监听组合式函数
 * 处理权限变更后的页面重定向和状态更新
 */

import { onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useMenu } from './useMenu'

export function usePermissionWatcher(options = {}) {
  const {
    // 是否自动重定向到403页面
    autoRedirectTo403 = true,
    // 是否显示权限变更提示
    showChangeNotification = true,
    // 自定义权限变更处理函数
    onPermissionChange = null
  } = options

  const router = useRouter()
  const route = useRoute()
  const authStore = useAuthStore()
  const { hasMenuPermission } = useMenu()

  let permissionChangeHandler = null

  /**
   * 检查当前页面权限
   */
  const checkCurrentPagePermission = () => {
    const currentPath = route.path
    
    // 跳过不需要权限检查的页面
    const publicPages = ['/login', '/403', '/404', '/']
    if (publicPages.includes(currentPath)) {
      return true
    }

    // 检查菜单权限
    return hasMenuPermission(currentPath)
  }

  /**
   * 处理权限变更
   */
  const handlePermissionChange = (event) => {
    const { permissions } = event.detail

    if (showChangeNotification) {
      ElMessage.info('权限已更新，正在检查页面访问权限...')
    }

    // 延迟执行，确保权限状态已更新
    setTimeout(() => {
      // 检查当前页面权限
      if (!checkCurrentPagePermission()) {
        if (autoRedirectTo403) {
          router.push('/403')
        }
      }

      // 执行自定义处理函数
      if (onPermissionChange && typeof onPermissionChange === 'function') {
        onPermissionChange(permissions)
      }
    }, 100)
  }

  /**
   * 手动刷新权限
   */
  const refreshPermissions = async () => {
    try {
      const success = await authStore.refreshPermissions()
      if (success) {
        ElMessage.success('权限已刷新')
      } else {
        ElMessage.warning('权限刷新失败')
      }
    } catch (error) {
      ElMessage.error('权限刷新失败')
    }
  }

  /**
   * 检查权限缓存状态
   */
  const checkPermissionCacheStatus = () => {
    if (!authStore.permissionCache.isValid()) {
      // 缓存已过期，可以选择自动刷新
      console.log('权限缓存已过期')
      return false
    }
    return true
  }

  /**
   * 监听页面可见性变化，页面重新激活时检查权限
   */
  const handleVisibilityChange = () => {
    if (document.visibilityState === 'visible') {
      // 页面重新可见时检查权限缓存
      if (!checkPermissionCacheStatus()) {
        // 可以选择自动刷新权限
        // refreshPermissions()
      }
    }
  }

  /**
   * 监听焦点变化
   */
  const handleFocusChange = () => {
    if (document.hasFocus()) {
      checkPermissionCacheStatus()
    }
  }

  onMounted(() => {
    // 监听权限变更事件
    permissionChangeHandler = handlePermissionChange
    window.addEventListener('permissionsChanged', permissionChangeHandler)
    
    // 监听页面可见性变化
    document.addEventListener('visibilitychange', handleVisibilityChange)
    
    // 监听页面焦点变化
    window.addEventListener('focus', handleFocusChange)
    
    // 初始检查
    checkPermissionCacheStatus()
  })

  onUnmounted(() => {
    // 清理事件监听器
    if (permissionChangeHandler) {
      window.removeEventListener('permissionsChanged', permissionChangeHandler)
    }
    document.removeEventListener('visibilitychange', handleVisibilityChange)
    window.removeEventListener('focus', handleFocusChange)
  })

  return {
    // 方法
    refreshPermissions,
    checkCurrentPagePermission,
    checkPermissionCacheStatus,
    
    // 状态
    authStore
  }
}

/**
 * 全局权限监听器
 * 在应用根组件中使用
 */
export function useGlobalPermissionWatcher() {
  return usePermissionWatcher({
    autoRedirectTo403: true,
    showChangeNotification: true,
    onPermissionChange: (permissions) => {
      console.log('全局权限变更:', permissions)
      
      // 可以在这里添加全局权限变更处理逻辑
      // 例如：更新菜单、刷新页面数据等
    }
  })
}