/**
 * 权限相关工具函数
 */

// 模块名称映射
export const MODULE_NAMES = {
  'user': '用户管理',
  'role': '角色管理', 
  'permission': '权限管理',
  'department': '部门管理',
  'group': '小组管理',
  'project': '项目管理',
  'task': '任务管理',
  'worklog': '工作日志',
  'notification': '通知消息',
  'statistics': '数据统计',
  'log': '操作日志',
  'system': '系统管理'
}

// 权限操作类型映射
export const PERMISSION_TYPES = {
  READ: ['read', 'list', 'view'],
  WRITE: ['create', 'add', 'update', 'edit', 'delete', 'remove'],
  CREATE: ['create', 'add'],
  UPDATE: ['update', 'edit'],
  DELETE: ['delete', 'remove']
}

// 权限模板配置
export const PERMISSION_TEMPLATES = {
  admin: {
    name: '管理员模板',
    description: '拥有所有权限',
    getPermissions: (permissions) => permissions.map(p => p.id)
  },
  editor: {
    name: '编辑者模板', 
    description: '拥有查看、创建、编辑权限',
    getPermissions: (permissions) => permissions
      .filter(p => 
        PERMISSION_TYPES.READ.some(type => p.code.includes(`:${type}`)) ||
        PERMISSION_TYPES.CREATE.some(type => p.code.includes(`:${type}`)) ||
        PERMISSION_TYPES.UPDATE.some(type => p.code.includes(`:${type}`))
      )
      .map(p => p.id)
  },
  viewer: {
    name: '查看者模板',
    description: '只拥有查看权限',
    getPermissions: (permissions) => permissions
      .filter(p => PERMISSION_TYPES.READ.some(type => p.code.includes(`:${type}`)))
      .map(p => p.id)
  }
}

/**
 * 获取模块中文名称
 * @param {string} module - 模块代码
 * @returns {string} 模块中文名称
 */
export const getModuleName = (module) => {
  return MODULE_NAMES[module] || module
}

/**
 * 获取权限标签类型
 * @param {string} code - 权限代码
 * @returns {string} Element Plus 标签类型
 */
export const getPermissionTagType = (code) => {
  if (PERMISSION_TYPES.CREATE.some(type => code.includes(`:${type}`))) return 'success'
  if (PERMISSION_TYPES.UPDATE.some(type => code.includes(`:${type}`))) return 'warning'
  if (PERMISSION_TYPES.DELETE.some(type => code.includes(`:${type}`))) return 'danger'
  if (PERMISSION_TYPES.READ.some(type => code.includes(`:${type}`))) return 'info'
  return 'primary'
}

/**
 * 获取权限图标组件名
 * @param {string} code - 权限代码
 * @returns {string} 图标组件名
 */
export const getPermissionIcon = (code) => {
  if (PERMISSION_TYPES.CREATE.some(type => code.includes(`:${type}`))) return 'Plus'
  if (PERMISSION_TYPES.UPDATE.some(type => code.includes(`:${type}`))) return 'Edit'
  if (PERMISSION_TYPES.DELETE.some(type => code.includes(`:${type}`))) return 'Delete'
  if (PERMISSION_TYPES.READ.some(type => code.includes(`:${type}`))) return 'View'
  return 'Key'
}

/**
 * 将权限列表按模块分组
 * @param {Array} permissions - 权限列表
 * @returns {Object} 按模块分组的权限对象
 */
export const groupPermissionsByModule = (permissions) => {
  const groups = {}
  permissions.forEach(permission => {
    const [module] = permission.code.split(':')
    if (!groups[module]) {
      groups[module] = []
    }
    groups[module].push(permission)
  })
  return groups
}

/**
 * 按权限类型过滤权限
 * @param {Array} permissions - 权限列表
 * @param {Array} types - 权限类型数组 (READ, WRITE, CREATE, UPDATE, DELETE)
 * @returns {Array} 过滤后的权限列表
 */
export const filterPermissionsByType = (permissions, types) => {
  const typeActions = types.flatMap(type => PERMISSION_TYPES[type] || [])
  return permissions.filter(p => 
    typeActions.some(action => p.code.includes(`:${action}`))
  )
}

/**
 * 按模块过滤权限
 * @param {Array} permissions - 权限列表
 * @param {Array} modules - 模块代码数组
 * @returns {Array} 过滤后的权限列表
 */
export const filterPermissionsByModule = (permissions, modules) => {
  return permissions.filter(p => {
    const [module] = p.code.split(':')
    return modules.includes(module)
  })
}

/**
 * 统计权限数量
 * @param {Array} permissions - 权限列表
 * @returns {Object} 权限统计信息
 */
export const getPermissionStats = (permissions) => {
  const modules = [...new Set(permissions.map(p => p.code.split(':')[0]))]
  const readCount = filterPermissionsByType(permissions, ['READ']).length
  const writeCount = filterPermissionsByType(permissions, ['WRITE']).length
  
  return {
    total: permissions.length,
    modules: modules.length,
    read: readCount,
    write: writeCount
  }
}

/**
 * 验证权限代码格式
 * @param {string} code - 权限代码
 * @returns {boolean} 是否为有效格式
 */
export const isValidPermissionCode = (code) => {
  return /^[a-zA-Z]+:[a-zA-Z]+$/.test(code)
}

/**
 * 生成权限代码
 * @param {string} module - 模块名
 * @param {string} action - 操作名
 * @returns {string} 权限代码
 */
export const generatePermissionCode = (module, action) => {
  return `${module}:${action}`
}