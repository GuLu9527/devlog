/**
 * 菜单管理组合式函数
 * 提供菜单相关的响应式数据和方法
 */

import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { menuConfig, menuUtils } from '@/config/menu'

export function useMenu() {
  const authStore = useAuthStore()

  /**
   * 计算过滤后的菜单项
   */
  const filteredMenuItems = computed(() => {
    // 权限未初始化时不显示菜单
    if (!authStore.hasInitialized) {
      if (process.env.NODE_ENV === 'development') {
        console.debug('菜单未显示：权限系统未初始化');
      }
      return []
    }
    
    // 用户未登录时不显示菜单
    if (!authStore.isLoggedIn) {
      if (process.env.NODE_ENV === 'development') {
        console.debug('菜单未显示：用户未登录');
      }
      return []
    }
    
    // 使用深拷贝避免修改原始配置
    const menuItems = JSON.parse(JSON.stringify(menuConfig))
    
    // 添加调试信息
    if (process.env.NODE_ENV === 'development') {
      console.debug('开始菜单权限过滤:', {
        userRole: authStore.user?.roleName,
        userPermissions: authStore.permissions,
        permissionCount: authStore.permissions?.length || 0
      });
    }
    
    const filtered = menuUtils.filterMenuByPermission(
      menuItems,
      authStore.hasPermission.bind(authStore),
      authStore.hasRole.bind(authStore)
    )
    
    // 添加过滤结果调试信息
    if (process.env.NODE_ENV === 'development') {
      console.debug('菜单过滤完成:', {
        originalCount: menuItems.length,
        filteredCount: filtered.length,
        filteredMenus: filtered.map(item => ({
          key: item.key,
          title: item.title,
          hasChildren: !!item.children,
          childrenCount: item.children?.length || 0
        }))
      });
    }
    
    return filtered
  })

  /**
   * 计算默认打开的子菜单
   */
  const defaultOpeneds = computed(() => {
    return menuUtils.getDefaultOpeneds(filteredMenuItems.value)
  })

  /**
   * 检查当前用户是否有某个菜单的访问权限
   * @param {string} path 菜单路径
   * @returns {boolean} 是否有权限
   */
  const hasMenuPermission = (path) => {
    const menuItem = menuUtils.findMenuByPath(menuConfig, path)
    if (!menuItem) return false
    
    return menuUtils.checkMenuPermission(
      menuItem.permission,
      authStore.hasPermission.bind(authStore)
    )
  }

  /**
   * 获取所有可访问的菜单路径
   */
  const accessiblePaths = computed(() => {
    return menuUtils.getAllMenuPaths(filteredMenuItems.value)
  })

  /**
   * 根据路径获取菜单项信息
   * @param {string} path 菜单路径
   * @returns {Object|null} 菜单项信息
   */
  const getMenuByPath = (path) => {
    return menuUtils.findMenuByPath(filteredMenuItems.value, path)
  }

  /**
   * 获取面包屑导航数据
   * @param {string} currentPath 当前路径
   * @returns {Array} 面包屑数组
   */
  const getBreadcrumbs = (currentPath) => {
    const breadcrumbs = []
    
    const findPath = (items, path, parents = []) => {
      for (const item of items) {
        const currentParents = [...parents, item]
        
        if (item.path === path) {
          breadcrumbs.push(...currentParents)
          return true
        }
        
        if (item.children) {
          if (findPath(item.children, path, currentParents)) {
            return true
          }
        }
      }
      return false
    }
    
    findPath(filteredMenuItems.value, currentPath)
    return breadcrumbs
  }

  return {
    // 响应式数据
    filteredMenuItems,
    defaultOpeneds,
    accessiblePaths,
    
    // 方法
    hasMenuPermission,
    getMenuByPath,
    getBreadcrumbs
  }
}