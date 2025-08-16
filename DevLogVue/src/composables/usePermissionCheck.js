/**
 * 权限检查组合式函数
 * 提供统一的权限验证和错误提示
 */

import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

export function usePermissionCheck() {
  const authStore = useAuthStore()

  /**
   * 检查权限并显示提示
   * @param {string|Array} permission 权限代码
   * @param {string} action 操作名称，用于错误提示
   * @param {boolean} showMessage 是否显示错误提示
   * @returns {boolean} 是否有权限
   */
  const checkPermission = (permission, action = '执行此操作', showMessage = true) => {
    const hasPermission = authStore.hasPermission(permission)
    
    if (!hasPermission && showMessage) {
      ElMessage.warning(`您没有${action}的权限`)
    }
    
    return hasPermission
  }

  /**
   * 检查角色权限并显示提示
   * @param {string|Array} role 角色名称
   * @param {string} action 操作名称，用于错误提示
   * @param {boolean} showMessage 是否显示错误提示
   * @returns {boolean} 是否有权限
   */
  const checkRole = (role, action = '执行此操作', showMessage = true) => {
    const hasRole = Array.isArray(role) 
      ? role.some(r => authStore.hasRole(r))
      : authStore.hasRole(role)
    
    if (!hasRole && showMessage) {
      ElMessage.warning(`您的角色无法${action}`)
    }
    
    return hasRole
  }

  /**
   * 检查多个权限（全部满足）
   * @param {Array} permissions 权限代码数组
   * @param {string} action 操作名称，用于错误提示
   * @param {boolean} showMessage 是否显示错误提示
   * @returns {boolean} 是否有权限
   */
  const checkAllPermissions = (permissions, action = '执行此操作', showMessage = true) => {
    const hasAllPermissions = authStore.hasAllPermissions(permissions)
    
    if (!hasAllPermissions && showMessage) {
      ElMessage.warning(`您没有${action}的完整权限`)
    }
    
    return hasAllPermissions
  }

  /**
   * 权限检查装饰器函数
   * @param {string|Array} permission 权限代码
   * @param {string} action 操作名称
   * @returns {Function} 装饰器函数
   */
  const requirePermission = (permission, action) => {
    return (fn) => {
      return (...args) => {
        if (checkPermission(permission, action)) {
          return fn(...args)
        }
      }
    }
  }

  /**
   * 角色检查装饰器函数
   * @param {string|Array} role 角色名称
   * @param {string} action 操作名称
   * @returns {Function} 装饰器函数
   */
  const requireRole = (role, action) => {
    return (fn) => {
      return (...args) => {
        if (checkRole(role, action)) {
          return fn(...args)
        }
      }
    }
  }

  /**
   * 常用权限检查方法
   */
  const permissions = {
    // CRUD 权限检查
    canCreate: (module) => checkPermission(`${module}:create`, '新增'),
    canRead: (module) => checkPermission(`${module}:list`, '查看'),
    canUpdate: (module) => checkPermission(`${module}:update`, '编辑'),
    canDelete: (module) => checkPermission(`${module}:delete`, '删除'),
    
    // 批量操作权限检查
    canBatchCreate: (module) => checkPermission(`${module}:batch:create`, '批量新增'),
    canBatchUpdate: (module) => checkPermission(`${module}:batch:update`, '批量编辑'),
    canBatchDelete: (module) => checkPermission(`${module}:batch:delete`, '批量删除'),
    
    // 导入导出权限检查
    canImport: (module) => checkPermission(`${module}:import`, '导入数据'),
    canExport: (module) => checkPermission(`${module}:export`, '导出数据'),
    
    // 审核权限检查
    canAudit: (module) => checkPermission(`${module}:audit`, '审核'),
    canApprove: (module) => checkPermission(`${module}:approve`, '审批'),
    
    // 状态变更权限检查
    canEnable: (module) => checkPermission(`${module}:enable`, '启用'),
    canDisable: (module) => checkPermission(`${module}:disable`, '禁用'),
    canReset: (module) => checkPermission(`${module}:reset`, '重置'),
  }

  /**
   * 页面级权限检查配置
   */
  const pagePermissions = {
    // 系统管理
    userManagement: () => checkPermission('user:list', '访问用户管理', false),
    roleManagement: () => checkPermission('role:list', '访问角色管理', false),
    permissionManagement: () => checkPermission('permission:list', '访问权限管理', false),
    
    // 项目管理
    projectManagement: () => checkPermission('project:list', '访问项目管理', false),
    taskManagement: () => checkPermission('task:list', '访问任务管理', false),
    
    // 组织管理
    departmentManagement: () => checkPermission('department:list', '访问部门管理', false),
    groupManagement: () => checkPermission('group:list', '访问小组管理', false),
    
    // 其他模块
    worklogManagement: () => checkPermission('worklog:list', '访问工作日志', false),
    skillTagManagement: () => checkPermission('skill:list', '访问技能标签', false),
    notificationManagement: () => checkPermission('notification:list', '访问通知消息', false),
  }

  return {
    // 基础权限检查
    checkPermission,
    checkRole,
    checkAllPermissions,
    
    // 装饰器函数
    requirePermission,
    requireRole,
    
    // 常用权限检查
    permissions,
    
    // 页面权限检查
    pagePermissions,
    
    // 直接访问 store
    authStore
  }
}