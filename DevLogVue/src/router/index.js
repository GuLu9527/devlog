import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "",
      component: () => import('@/views/layout/index.vue'),
      redirect: "/index",
      children: [
        {
          path: "index",
          name: "index",
          component: () => import('@/views/index/index.vue'),
          meta: { 
            title: '首页', 
            icon: 'House',
            permissions: [], // 无权限要求
            requireAuth: true
          }
        },
        {
          path: "user",
          name: "user",
          component: () => import('@/views/user/index.vue'),
          meta: { 
            title: '用户管理', 
            icon: 'User',
            permissions: ['user:list'],
            requireAuth: true
          }
        },
        {
          path: "role",
          name: "role",
          component: () => import('@/views/role/index.vue'),
          meta: { 
            title: '角色管理', 
            icon: 'UserFilled',
            permissions: ['role:list'],
            requireAuth: true
          }
        },
        {
          path: "permission",
          name: "permission",
          component: () => import('@/views/permission/index.vue'),
          meta: { 
            title: '权限管理', 
            icon: 'Lock',
            permissions: ['permission:list'],
            requireAuth: true
          }
        },
        {
          path: "project",
          name: "project",
          component: () => import('@/views/project/index.vue'),
          meta: { 
            title: '项目管理', 
            icon: 'Folder',
            permissions: ['project:list'],
            requireAuth: true
          }
        },
        {
          path: "task",
          name: "task",
          component: () => import('@/views/task/index.vue'),
          meta: { 
            title: '任务管理', 
            icon: 'Calendar',
            permissions: ['task:list'],
            requireAuth: true
          }
        },
        {
          path: "project/dashboard",
          name: "projectDashboard",
          component: () => import('@/views/project/dashboard.vue'),
          meta: { 
            title: '项目仪表板', 
            icon: 'DataAnalysis',
            permissions: ['project:view'],
            requireAuth: true
          }
        },
        {
          path: "task/dependency",
          name: "taskDependency",
          component: () => import('@/views/task/dependency.vue'),
          meta: { 
            title: '任务依赖管理', 
            icon: 'Connection',
            permissions: ['task:dependency:view'],
            requireAuth: true
          }
        },
        {
          path: "task/gantt",
          name: "taskGantt",
          component: () => import('@/views/task/gantt.vue'),
          meta: { 
            title: '甘特图', 
            icon: 'TrendCharts',
            permissions: ['task:list'],
            requireAuth: true
          }
        },
        {
          path: "task/template",
          name: "taskTemplate",
          component: () => import('@/views/task/template.vue'),
          meta: { 
            title: '任务模板', 
            icon: 'Files',
            permissions: ['task:template:list'],
            requireAuth: true
          }
        },
        {
          path: "work-hours",
          name: "workHours",
          component: () => import('@/views/workHours/index.vue').catch(err => {
            console.error('Failed to load workHours page:', err);
            return import('@/views/error/404.vue'); // 后备页面
          }),
          meta: { 
            title: '工时统计', 
            icon: 'Timer',
            permissions: ['work:hour:view'],
            requireAuth: true
          }
        },
        {
          path: "department",
          name: "department",
          component: () => import('@/views/department/index.vue'),
          meta: { 
            title: '部门管理', 
            icon: 'OfficeBuilding',
            permissions: ['department:list'],
            requireAuth: true
          }
        },
        {
          path: "group",
          name: "group",
          component: () => import('@/views/group/index.vue'),
          meta: { 
            title: '小组管理', 
            icon: 'Avatar',
            permissions: ['group:list'],
            requireAuth: true
          }
        },
        {
          path: "worklog",
          name: "worklog",
          component: () => import('@/views/worklog/index.vue'),
          meta: { 
            title: '工作日志', 
            icon: 'Document',
            permissions: ['worklog:list'],
            requireAuth: true
          }
        },
        {
          path: "notification",
          name: "notification",
          component: () => import('@/views/notification/index.vue'),
          meta: { 
            title: '通知消息', 
            icon: 'Bell',
            permissions: ['notification:list'],
            requireAuth: true
          }
        },
        {
          path: "skill-tag",
          name: "skillTag",
          component: () => import('@/views/skillTag/index.vue'),
          meta: { 
            title: '技能标签', 
            icon: 'CollectionTag',
            permissions: ['skilltag:list'],
            requireAuth: true
          }
        },
        {
          path: "debug",
          name: "debug",
          component: () => import('@/views/debug/index.vue'),
          meta: { 
            title: '调试工具', 
            icon: 'Tools',
            permissions: ['system:debug'],
            requireAuth: true
          }
        },
        {
          path: "debug/permissions",
          name: "debugPermissions",
          component: () => import('@/views/debug/permissions.vue'),
          meta: { 
            title: '权限调试', 
            icon: 'Tools',
            permissions: [],
            requireAuth: true
          }
        },
      ],
    },
    {
      path: "/login",
      name: "login",
      component: () => import('@/views/login/index.vue'),
      meta: { requireAuth: false }
    },
    {
      path: "/register", 
      name: "register",
      component: () => import('@/views/register/index.vue'),
      meta: { requireAuth: false }
    },
    {
      path: "/403",
      name: "403",
      component: () => import('@/views/error/403.vue'),
      meta: { 
        title: '无权限访问',
        requireAuth: false 
      }
    },
  ],
});

// 导入权限相关模块
import { useAuthStore } from '@/stores/auth'
import { canAccessPage } from '@/utils/permission'

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - DevLog` : 'DevLog'
  
  const authStore = useAuthStore()
  const token = localStorage.getItem('token')
  
  // 初始化用户权限信息（如果未初始化且有token）
  if (token && !authStore.hasInitialized) {
    const initSuccess = authStore.initializeFromStorage()
    if (!initSuccess) {
      // 初始化失败，清除token并跳转登录
      authStore.clearAuth()
      next('/login')
      return
    }
  }
  
  // 不需要认证的路由
  if (to.meta.requireAuth === false) {
    // 已登录用户访问登录/注册页面时跳转到首页
    if (['/login', '/register'].includes(to.path) && token) {
      next('/index')
    } else {
      next()
    }
    return
  }
  
  // 需要认证的路由
  if (!token) {
    next('/login')
    return
  }
  
  // 检查页面权限
  const requiredPermissions = to.meta.permissions || []
  
  // 如果页面不需要特定权限，直接放行
  if (requiredPermissions.length === 0) {
    next()
    return
  }
  
  // 检查用户是否有所需权限
  const hasPermission = authStore.hasPermission(requiredPermissions)
  
  if (hasPermission) {
    next()
  } else {
    // 权限不足，跳转到403页面
    console.warn(`权限不足，无法访问页面: ${to.path}，需要权限: ${requiredPermissions}`)
    next('/403')
  }
})

export default router;
