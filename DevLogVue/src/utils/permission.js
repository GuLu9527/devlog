import { useAuthStore } from '@/stores/auth'
import { PAGE_PERMISSIONS, MENU_CONFIG } from '@/stores/permissions'

/**
 * 权限验证工具函数
 */

/**
 * 检查是否有指定权限
 * @param {string|Array} permission 权限代码或权限数组
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const authStore = useAuthStore()
  return authStore.hasPermission(permission)
}

/**
 * 检查是否有所有指定权限
 * @param {Array} permissions 权限数组
 * @returns {boolean}
 */
export function hasAllPermissions(permissions) {
  const authStore = useAuthStore()
  return authStore.hasAllPermissions(permissions)
}

/**
 * 检查用户是否有访问指定页面的权限
 * @param {string} path 页面路径
 * @returns {boolean}
 */
export function canAccessPage(path) {
  const requiredPermissions = PAGE_PERMISSIONS[path]
  
  // 如果页面不需要权限，直接返回true
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true
  }
  
  const authStore = useAuthStore()
  
  // 检查用户是否有所需权限（满足任意一个即可）
  return authStore.hasPermission(requiredPermissions)
}

/**
 * 检查用户是否有指定角色
 * @param {string} roleName 角色名称
 * @returns {boolean}
 */
export function hasRole(roleName) {
  const authStore = useAuthStore()
  return authStore.hasRole(roleName)
}

/**
 * 获取用户可以访问的菜单列表
 * @returns {Array} 菜单配置数组
 */
export function getAccessibleMenus() {
  const authStore = useAuthStore()
  
  return MENU_CONFIG.filter(menu => {
    // 如果菜单被隐藏，不显示
    if (menu.hidden) return false
    
    // 如果菜单不需要权限，显示
    if (!menu.permissions || menu.permissions.length === 0) return true
    
    // 检查用户是否有菜单所需权限
    return authStore.hasPermission(menu.permissions)
  }).sort((a, b) => a.order - b.order) // 按order排序
}

/**
 * 检查是否为管理员
 * @returns {boolean}
 */
export function isAdmin() {
  const authStore = useAuthStore()
  return authStore.hasRole('管理员') || authStore.hasRole('ADMIN')
}

/**
 * 获取用户权限分组
 * @returns {Object} 权限分组对象
 */
export function getUserPermissionGroups() {
  const authStore = useAuthStore()
  const userPermissions = authStore.userPermissions
  
  const groups = {}
  
  // 遍历权限分组，检查用户拥有的权限
  Object.entries(PERMISSION_GROUPS).forEach(([key, group]) => {
    const hasGroupPermissions = group.permissions.filter(p => 
      userPermissions.includes(p)
    )
    
    if (hasGroupPermissions.length > 0) {
      groups[key] = {
        ...group,
        userPermissions: hasGroupPermissions
      }
    }
  })
  
  return groups
}

/**
 * 权限检查装饰器，用于方法级权限控制
 * @param {string|Array} requiredPermissions 所需权限
 */
export function requirePermission(requiredPermissions) {
  return function(target, propertyKey, descriptor) {
    const originalMethod = descriptor.value
    
    descriptor.value = function(...args) {
      if (hasPermission(requiredPermissions)) {
        return originalMethod.apply(this, args)
      } else {
        console.warn(`权限不足，需要权限: ${JSON.stringify(requiredPermissions)}`)
        return false
      }
    }
    
    return descriptor
  }
}

/**
 * 根据权限过滤数组数据
 * @param {Array} items 数据数组
 * @param {Function} permissionGetter 获取权限的函数
 * @returns {Array} 过滤后的数组
 */
export function filterByPermission(items, permissionGetter) {
  return items.filter(item => {
    const requiredPermission = permissionGetter(item)
    return !requiredPermission || hasPermission(requiredPermission)
  })
}