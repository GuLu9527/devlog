import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 认证和权限管理 Store
 */
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref(null)
  const permissions = ref([])
  const hasInitialized = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!user.value)
  const userPermissions = computed(() => permissions.value || [])

  // 方法
  const setUser = (userData) => {
    user.value = userData
    permissions.value = userData?.permissions || []
    hasInitialized.value = true
    
    // 同步到localStorage
    if (userData) {
      localStorage.setItem('userInfo', JSON.stringify(userData))
      // 设置权限缓存
      if (userData.permissions) {
        permissionCache.set(userData.permissions)
      }
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  const setPermissions = (perms) => {
    permissions.value = perms || []
  }

  const hasPermission = (permission) => {
    // 空权限要求视为无限制
    if (!permission) {
      return true;
    }
    
    // 权限未初始化时返回false
    if (!permissions.value || permissions.value.length === 0) {
      if (process.env.NODE_ENV === 'development') {
        console.debug('权限检查失败：用户权限未初始化或为空');
      }
      return false;
    }
    
    // 支持数组或字符串权限检查
    if (Array.isArray(permission)) {
      const result = permission.some(p => permissions.value.includes(p));
      if (process.env.NODE_ENV === 'development') {
        console.debug(`权限检查（数组）: ${JSON.stringify(permission)} -> ${result}`, {
          userPermissions: permissions.value,
          requiredPermissions: permission
        });
      }
      return result;
    }
    
    const result = permissions.value.includes(permission);
    if (process.env.NODE_ENV === 'development') {
      console.debug(`权限检查（单个）: ${permission} -> ${result}`, {
        userPermissions: permissions.value
      });
    }
    return result;
  }

  const hasAllPermissions = (permissionList) => {
    if (!permissionList || permissionList.length === 0) return true
    if (!permissions.value || permissions.value.length === 0) return false
    
    return permissionList.every(p => permissions.value.includes(p))
  }

  const hasRole = (roleName) => {
    return user.value?.roleName === roleName
  }

  const initializeFromStorage = () => {
    try {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        const userData = JSON.parse(userInfo)
        
        // 确保权限数据存在
        if (!userData.permissions || !Array.isArray(userData.permissions)) {
          console.warn('用户数据中缺少权限信息，尝试从缓存获取')
          
          // 尝试从缓存中获取权限数据
          const cachedPermissions = permissionCache.get()
          if (cachedPermissions && userData.id === user.value?.id) {
            userData.permissions = cachedPermissions
          } else {
            // 如果缓存也没有，设置为空数组并标记需要刷新
            userData.permissions = []
            console.warn('权限数据为空，需要重新获取')
          }
        }
        
        setUser(userData)
        
        // 验证权限数据的有效性
        if (process.env.NODE_ENV === 'development') {
          console.debug('用户权限初始化完成:', {
            userId: userData.id,
            username: userData.username,
            roleName: userData.roleName,
            permissionCount: userData.permissions?.length || 0,
            permissions: userData.permissions
          });
        }
        
        return true
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
      clearAuth()
    }
    
    hasInitialized.value = true // 即使失败也标记为已初始化，避免无限等待
    return false
  }

  const clearAuth = () => {
    user.value = null
    permissions.value = []
    hasInitialized.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    permissionCache.clear()
  }

  const updatePermissions = (newPermissions) => {
    permissions.value = newPermissions || []
    
    // 更新localStorage中的用户信息
    if (user.value) {
      const updatedUser = { ...user.value, permissions: newPermissions }
      setUser(updatedUser)
    }
    
    // 触发权限变更事件
    window.dispatchEvent(new CustomEvent('permissionsChanged', {
      detail: { permissions: newPermissions }
    }))
  }

  // 刷新权限数据
  const refreshPermissions = async () => {
    if (!user.value?.id) return false
    
    try {
      // 这里应该调用获取用户权限的API
      // const response = await getUserPermissions(user.value.id)
      // updatePermissions(response.data.permissions)
      
      // 暂时使用本地数据，实际项目中应该从服务器获取最新权限
      console.log('权限数据已刷新')
      return true
    } catch (error) {
      console.error('刷新权限失败:', error)
      return false
    }
  }

  // 权限缓存管理
  const permissionCache = {
    // 缓存过期时间（毫秒）
    CACHE_EXPIRE_TIME: 30 * 60 * 1000, // 30分钟
    
    // 获取缓存的权限数据
    get() {
      try {
        const cached = localStorage.getItem('permission_cache')
        if (!cached) return null
        
        const data = JSON.parse(cached)
        const now = Date.now()
        
        // 检查是否过期
        if (now > data.expireAt) {
          this.clear()
          return null
        }
        
        return data.permissions
      } catch (error) {
        console.error('读取权限缓存失败:', error)
        return null
      }
    },
    
    // 设置权限缓存
    set(permissions) {
      try {
        const data = {
          permissions,
          expireAt: Date.now() + this.CACHE_EXPIRE_TIME,
          userId: user.value?.id
        }
        localStorage.setItem('permission_cache', JSON.stringify(data))
      } catch (error) {
        console.error('设置权限缓存失败:', error)
      }
    },
    
    // 清除权限缓存
    clear() {
      localStorage.removeItem('permission_cache')
    },
    
    // 检查缓存是否有效
    isValid() {
      const cached = this.get()
      return cached !== null
    }
  }

  // 获取用户拥有的菜单权限
  const getMenuPermissions = () => {
    return permissions.value.filter(p => 
      p.includes(':list') || p.includes(':read') || p === 'system:debug'
    )
  }

  return {
    // 状态
    user,
    permissions,
    hasInitialized,
    
    // 计算属性
    isLoggedIn,
    userPermissions,
    
    // 方法
    setUser,
    setPermissions,
    hasPermission,
    hasAllPermissions,
    hasRole,
    initializeFromStorage,
    clearAuth,
    updatePermissions,
    refreshPermissions,
    getMenuPermissions,
    
    // 权限缓存
    permissionCache
  }
})