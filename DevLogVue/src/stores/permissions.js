/**
 * 权限相关常量配置
 */

// 页面权限映射 - 定义每个页面需要的权限
export const PAGE_PERMISSIONS = {
  '/index': [],                           // 首页，所有人可访问
  '/user': ['user:list'],                 // 用户管理页面
  '/role': ['role:list'],                 // 角色管理页面  
  '/permission': ['permission:list'],     // 权限管理页面
  '/project': ['project:list'],           // 项目管理页面
  '/task': ['task:list'],                 // 任务管理页面
  '/department': ['department:list'],     // 部门管理页面
  '/group': ['group:list'],               // 小组管理页面
  '/worklog': ['worklog:list'],           // 工作日志页面
  '/notification': ['notification:list'], // 通知消息页面
  '/skill-tag': ['skilltag:list'],           // 技能标签页面
  '/debug': ['system:debug']              // 调试工具页面（管理员专用）
}

// 菜单配置 - 定义菜单项和对应权限
export const MENU_CONFIG = [
  {
    path: '/index',
    name: 'index',
    title: '首页',
    icon: 'House',
    permissions: [], // 无权限要求，所有人可访问
    hidden: false,
    order: 1
  },
  {
    path: '/user',
    name: 'user',
    title: '用户管理',
    icon: 'User',
    permissions: ['user:list'],
    hidden: false,
    order: 2
  },
  {
    path: '/role',
    name: 'role',
    title: '角色管理',
    icon: 'UserFilled',
    permissions: ['role:list'],
    hidden: false,
    order: 3
  },
  {
    path: '/permission',
    name: 'permission',
    title: '权限管理',
    icon: 'Lock',
    permissions: ['permission:list'],
    hidden: false,
    order: 4
  },
  {
    path: '/department',
    name: 'department',
    title: '部门管理',
    icon: 'OfficeBuilding',
    permissions: ['department:list'],
    hidden: false,
    order: 5
  },
  {
    path: '/group',
    name: 'group',
    title: '小组管理',
    icon: 'Avatar',
    permissions: ['group:list'],
    hidden: false,
    order: 6
  },
  {
    path: '/project',
    name: 'project',
    title: '项目管理',
    icon: 'Folder',
    permissions: ['project:list'],
    hidden: false,
    order: 7
  },
  {
    path: '/task',
    name: 'task',
    title: '任务管理',
    icon: 'Calendar',
    permissions: ['task:list'],
    hidden: false,
    order: 8
  },
  {
    path: '/worklog',
    name: 'worklog',
    title: '工作日志',
    icon: 'Document',
    permissions: ['worklog:list'],
    hidden: false,
    order: 9
  },
  {
    path: '/notification',
    name: 'notification',
    title: '通知消息',
    icon: 'Bell',
    permissions: ['notification:list'],
    hidden: false,
    order: 10
  },
  {
    path: '/skill-tag',
    name: 'skillTag',
    title: '技能标签',
    icon: 'CollectionTag',
    permissions: ['skilltag:list'],
    hidden: false,
    order: 11
  },
  {
    path: '/debug',
    name: 'debug',
    title: '调试工具',
    icon: 'Tools',
    permissions: ['system:debug'],
    hidden: false,
    order: 12
  }
]

// 权限分组 - 用于管理和展示
export const PERMISSION_GROUPS = {
  USER: {
    name: '用户管理',
    permissions: ['user:list', 'user:create', 'user:update', 'user:delete', 'user:read']
  },
  ROLE: {
    name: '角色管理', 
    permissions: ['role:list', 'role:create', 'role:update', 'role:delete']
  },
  PERMISSION: {
    name: '权限管理',
    permissions: ['permission:list', 'permission:create', 'permission:update', 'permission:delete']
  },
  DEPARTMENT: {
    name: '部门管理',
    permissions: ['department:list', 'department:create', 'department:update', 'department:delete']
  },
  GROUP: {
    name: '小组管理',
    permissions: ['group:list', 'group:create', 'group:update', 'group:delete']
  },
  PROJECT: {
    name: '项目管理',
    permissions: ['project:list', 'project:create', 'project:update', 'project:delete']
  },
  TASK: {
    name: '任务管理',
    permissions: ['task:list', 'task:create', 'task:update', 'task:delete']
  },
  WORKLOG: {
    name: '工作日志',
    permissions: ['worklog:list', 'worklog:create', 'worklog:update', 'worklog:delete', 'worklog:review']
  },
  NOTIFICATION: {
    name: '通知管理',
    permissions: [
      'notification:list', 'notification:read', 'notification:update', 
      'notification:mark-read', 'notification:mark-all-read', 
      'notification:delete', 'notification:send', 'notification:batch-send'
    ]
  },
  SKILLTAG: {
    name: '技能标签',
    permissions: ['skilltag:list', 'skilltag:read', 'skilltag:create', 'skilltag:update', 'skilltag:delete']
  },
  SYSTEM: {
    name: '系统管理',
    permissions: ['system:debug', 'system:config']
  }
}

// 无需登录的路由
export const NO_AUTH_ROUTES = ['/login', '/register']

// 默认重定向路由
export const DEFAULT_ROUTE = '/index'

// 无权限时的重定向路由
export const NO_PERMISSION_ROUTE = '/403'