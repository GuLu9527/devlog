/**
 * 菜单配置文件
 * 统一管理系统菜单结构和权限配置
 * 
 * 权限说明：
 * - permission: null 表示无权限限制，所有用户可见
 * - permission: string 表示单一权限要求
 * - permission: array 表示多权限任一满足即可（OR逻辑）
 * - requireAllPermissions: true 表示需要所有权限都满足（AND逻辑）
 * - adminOnly: true 表示仅管理员可见
 */

export const menuConfig = [
  {
    key: 'home',
    path: '/index',
    icon: 'home',
    title: '工作台',
    permission: null // 首页无权限限制，所有用户可访问
  },
  {
    key: 'project',
    title: '项目协作',
    icon: 'project',
    // 项目管理菜单：普通用户也可以访问，但只能看到分配给自己的项目
    permission: 'project:list',
    adminOnly: false,
    children: [
      {
        key: 'project',
        path: '/project',
        icon: 'project',
        title: '项目管理',
        permission: 'project:list'
      }
    ]
  },
  {
    key: 'task-management',
    title: '任务管理',
    icon: 'task',
    permission: ['task:list', 'task:dependency:view', 'task:template:list'],
    requireAllPermissions: false,
    adminOnly: false,
    children: [
      {
        key: 'task',
        path: '/task',
        icon: 'task',
        title: '任务列表',
        permission: 'task:list'
      },
      {
        key: 'task-dependency',
        path: '/task/dependency',
        icon: 'link',
        title: '依赖关系',
        permission: 'task:dependency:view'
      },
      {
        key: 'task-template',
        path: '/task/template',
        icon: 'template',
        title: '任务模板',
        permission: 'task:template:list'
      }
    ]
  },
  {
    key: 'visualization',
    title: '数据可视化',
    icon: 'analytics',
    permission: ['work:hour:view', 'project:view', 'task:list'],
    requireAllPermissions: false,
    adminOnly: false,
    children: [
      {
        key: 'project-dashboard',
        path: '/project/dashboard',
        icon: 'dashboard',
        title: '项目仪表板',
        permission: 'project:view'
      },
      {
        key: 'task-gantt',
        path: '/task/gantt',
        icon: 'timeline',
        title: '甘特图',
        permission: 'task:list'
      },
      {
        key: 'work-hours',
        path: '/work-hours',
        icon: 'clock',
        title: '工时统计',
        permission: 'work:hour:view'
      }
    ]
  },
  {
    key: 'personal',
    title: '个人中心',
    icon: 'user',
    permission: null,
    requireAllPermissions: false,
    adminOnly: false,
    children: [
      {
        key: 'worklog',
        path: '/worklog',
        icon: 'worklog',
        title: '工作日志',
        permission: 'worklog:list'
      },
      {
        key: 'notification',
        path: '/notification',
        icon: 'notification',
        title: '通知消息',
        permission: 'notification:list'
      },
      {
        key: 'skillTag',
        path: '/skill-tag',
        icon: 'tag',
        title: '技能标签',
        permission: 'skilltag:list'
      }
    ]
  },
  {
    key: 'organization',
    title: '组织管理',
    icon: 'department',
    // 组织管理菜单：需要部门或小组管理权限，普通用户不应看到
    permission: ['department:list', 'group:list'],
    requireAllPermissions: false, // 任一组织权限即可显示父菜单
    adminOnly: false,
    children: [
      {
        key: 'department',
        path: '/department',
        icon: 'department',
        title: '部门管理',
        permission: 'department:list'
      },
      {
        key: 'group',
        path: '/group',
        icon: 'group',
        title: '小组管理',
        permission: 'group:list'
      }
    ]
  },
  {
    key: 'system',
    title: '系统管理',
    icon: 'setting',
    // 系统管理菜单：严格要求管理权限，普通用户不应看到
    permission: ['user:list', 'role:list', 'permission:list'],
    requireAllPermissions: false, // 任一管理权限即可显示父菜单
    adminOnly: false, // 不仅限管理员，部门经理也可以有部分管理权限
    children: [
      {
        key: 'user',
        path: '/user',
        icon: 'user',
        title: '用户管理',
        permission: 'user:list'
      },
      {
        key: 'role',
        path: '/role',
        icon: 'role',
        title: '角色管理',
        permission: 'role:list'
      },
      {
        key: 'permission',
        path: '/permission',
        icon: 'permission',
        title: '权限管理',
        permission: 'permission:list'
      }
    ]
  }
];

/**
 * 菜单工具函数
 */
export const menuUtils = {
  /**
   * 根据权限过滤菜单项
   * @param {Array} items 菜单项
   * @param {Function} hasPermission 权限检查函数
   * @param {Function} hasRole 角色检查函数
   * @returns {Array} 过滤后的菜单项
   */
  filterMenuByPermission(items, hasPermission, hasRole = null) {
    const filteredItems = [];
    
    for (const item of items) {
      // 首先检查管理员专用菜单
      if (item.adminOnly && hasRole && !hasRole('ADMIN') && !hasRole('管理员')) {
        if (process.env.NODE_ENV === 'development') {
          console.debug(`菜单 ${item.title} 仅管理员可访问，当前用户无权限`);
        }
        continue;
      }
      
      // 检查当前菜单项权限
      const hasCurrentPermission = this.checkMenuPermission(item, hasPermission);
      
      // 添加调试信息（开发环境）
      if (process.env.NODE_ENV === 'development') {
        console.debug(`菜单权限检查: ${item.title}(${item.key}) - 需要权限: ${JSON.stringify(item.permission)} - 要求全部权限: ${item.requireAllPermissions || false} - 检查结果: ${hasCurrentPermission}`);
      }
      
      if (!hasCurrentPermission) {
        continue;
      }
      
      // 创建新的菜单项对象
      const newItem = {
        key: item.key,
        path: item.path,
        icon: item.icon,
        title: item.title,
        permission: item.permission,
        requireAllPermissions: item.requireAllPermissions,
        adminOnly: item.adminOnly
      };
      
      // 如果有子菜单，递归过滤子菜单
      if (item.children && item.children.length > 0) {
        const filteredChildren = this.filterMenuByPermission(item.children, hasPermission, hasRole);
        
        // 严格检查：如果是管理类菜单且子菜单全部被过滤，则父菜单也不显示
        if (filteredChildren.length === 0) {
          if (process.env.NODE_ENV === 'development') {
            console.debug(`父菜单 ${item.title} 因子菜单全部被过滤而隐藏`);
          }
          continue;
        }
        
        // 额外检查：对于管理类菜单，确保用户确实有管理权限
        if ((item.key === 'system' || item.key === 'organization') && !this.hasManagementPermissions(item.permission, hasPermission)) {
          if (process.env.NODE_ENV === 'development') {
            console.debug(`管理菜单 ${item.title} 用户无实际管理权限，隐藏菜单`);
          }
          continue;
        }
        
        newItem.children = filteredChildren;
      }
      
      filteredItems.push(newItem);
    }
    
    return filteredItems;
  },

  /**
   * 检查菜单权限
   * @param {Object|string|Array|null} item 菜单项或权限配置
   * @param {Function} hasPermission 权限检查函数
   * @returns {boolean} 是否有权限
   */
  checkMenuPermission(item, hasPermission) {
    // 兼容旧版本调用方式
    let permission = item;
    let requireAllPermissions = false;
    
    if (typeof item === 'object' && item !== null && !Array.isArray(item)) {
      permission = item.permission;
      requireAllPermissions = item.requireAllPermissions || false;
    }
    
    if (!permission) {
      return true; // 无权限限制
    }
    
    if (Array.isArray(permission)) {
      if (requireAllPermissions) {
        // 需要所有权限都满足（AND逻辑）
        return permission.every(p => hasPermission(p));
      } else {
        // 任一权限满足即可（OR逻辑）
        return hasPermission(permission);
      }
    }
    
    // 单个权限
    return hasPermission(permission);
  },

  /**
   * 检查是否有管理权限（用于额外的管理菜单验证）
   * @param {Array|string} permissions 权限列表
   * @param {Function} hasPermission 权限检查函数
   * @returns {boolean} 是否有管理权限
   */
  hasManagementPermissions(permissions, hasPermission) {
    if (!permissions) return false;
    
    const managementPermissions = Array.isArray(permissions) ? permissions : [permissions];
    
    // 检查是否有任何管理权限
    return managementPermissions.some(permission => hasPermission(permission));
  },

  /**
   * 获取默认打开的子菜单
   * @param {Array} menuItems 菜单项
   * @returns {Array} 默认打开的菜单key数组
   */
  getDefaultOpeneds(menuItems) {
    return menuItems
      .filter(item => item.children && item.children.length > 0)
      .map(item => item.key);
  },

  /**
   * 根据路径查找菜单项
   * @param {Array} menuItems 菜单项
   * @param {string} path 路径
   * @returns {Object|null} 找到的菜单项
   */
  findMenuByPath(menuItems, path) {
    for (const item of menuItems) {
      if (item.path === path) {
        return item;
      }
      if (item.children) {
        const found = this.findMenuByPath(item.children, path);
        if (found) return found;
      }
    }
    return null;
  },

  /**
   * 获取菜单路径
   * @param {Array} menuItems 菜单项
   * @returns {Array} 所有菜单路径数组
   */
  getAllMenuPaths(menuItems) {
    const paths = [];
    
    const traverse = (items) => {
      items.forEach(item => {
        if (item.path) {
          paths.push(item.path);
        }
        if (item.children) {
          traverse(item.children);
        }
      });
    };
    
    traverse(menuItems);
    return paths;
  }
};