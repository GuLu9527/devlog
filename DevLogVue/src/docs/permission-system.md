# 前端权限控制系统使用文档

## 概述

本系统提供了完整的前端权限控制解决方案，支持路由级、页面级、组件级和元素级的权限控制，采用iOS风格设计，具有良好的用户体验。

## 核心特性

### 🔐 多层级权限控制
- **路由级权限**：通过路由守卫控制页面访问
- **菜单级权限**：基于用户权限动态生成菜单
- **页面级权限**：组件级权限验证
- **元素级权限**：按钮、链接等元素的显示控制

### 🎨 优秀的用户体验
- **iOS风格设计**：统一的视觉风格
- **智能提示**：权限不足时的友好提示
- **缓存机制**：30分钟权限缓存，提升性能
- **实时更新**：权限变更时自动刷新

### 🚀 高度可复用
- **组合式函数**：提供通用的权限检查逻辑
- **CRUD模板**：标准化的增删改查页面模板
- **权限指令**：v-permission、v-role指令
- **组件库**：丰富的权限相关组件

## 架构设计

```
src/
├── stores/
│   └── auth.js                 # 权限状态管理
├── composables/
│   ├── useMenu.js             # 菜单管理
│   ├── usePermissionCheck.js  # 权限检查
│   ├── usePermissionWatcher.js # 权限监听
│   └── useCrudPage.js         # CRUD页面模板
├── components/
│   └── Permission/
│       ├── PermissionCheck.vue     # 页面级权限检查
│       ├── PermissionGuard.vue     # 权限保护组件
│       └── PermissionTooltip.vue   # 权限提示组件
├── directives/
│   └── permission.js          # 权限指令
├── config/
│   └── menu.js               # 菜单配置
└── router/
    └── permission.js         # 路由权限守卫
```

## 快速开始

### 1. 基础配置

在 `main.js` 中注册权限指令：

```javascript
import { createApp } from 'vue'
import permissionDirective from '@/directives/permission'

const app = createApp(App)
app.use(permissionDirective)
```

### 2. 菜单配置

在 `src/config/menu.js` 中配置菜单权限：

```javascript
export const menuConfig = [
  {
    key: 'user',
    path: '/user',
    icon: 'user',
    title: '用户管理',
    permission: 'user:list'
  },
  {
    key: 'system',
    title: '系统管理',
    icon: 'setting',
    permission: ['user:list', 'role:list'], // 任一权限即可
    children: [
      // 子菜单...
    ]
  }
]
```

### 3. 权限初始化

在登录成功后设置用户权限：

```javascript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 登录成功后
const loginSuccess = (userData) => {
  authStore.setUser(userData) // userData 包含 permissions 数组
}
```

## 使用指南

### 权限指令

#### v-permission 指令

```vue
<!-- 单个权限 -->
<el-button v-permission="'user:create'">新增用户</el-button>

<!-- 多个权限（任一满足） -->
<el-button v-permission="['user:create', 'user:update']">操作</el-button>

<!-- 多个权限（全部满足） -->
<el-button v-permission.all="['user:create', 'user:update']">操作</el-button>

<!-- 禁用而非隐藏 -->
<el-button v-permission.disable="'user:delete'">删除</el-button>
```

#### v-role 指令

```vue
<!-- 单个角色 -->
<el-button v-role="'ADMIN'">管理员功能</el-button>

<!-- 多个角色 -->
<el-button v-role="['ADMIN', 'MANAGER']">高级功能</el-button>
```

### 页面级权限控制

#### PermissionCheck 组件

```vue
<template>
  <PermissionCheck permission="user:list">
    <!-- 有权限时显示的内容 -->
    <UserManagement />
    
    <!-- 无权限时显示的内容（可选） -->
    <template #no-permission>
      <div>您没有访问用户管理的权限</div>
    </template>
  </PermissionCheck>
</template>
```

#### PermissionGuard 组件（增强版）

```vue
<template>
  <PermissionGuard 
    permission="user:list"
    :show-details="true"
    @access-denied="handleAccessDenied"
  >
    <UserManagement />
  </PermissionGuard>
</template>
```

### CRUD页面快速开发

使用 `useCrudPage` 组合式函数快速构建标准CRUD页面：

```vue
<script setup>
import { useCrudPage } from '@/composables/useCrudPage'
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'

const {
  // 状态
  loading, tableData, dialogVisible,
  // 方法
  getList, handleAdd, handleEdit, handleDelete, handleSubmit
} = useCrudPage({
  module: 'user',
  apiMethods: {
    list: getUserList,
    create: createUser,
    update: updateUser,
    delete: deleteUser
  },
  defaultForm: { name: '', email: '', status: 1 }
})
</script>
```

### 权限检查工具函数

```javascript
import { usePermissionCheck } from '@/composables/usePermissionCheck'

const { permissions, checkPermission } = usePermissionCheck()

// 在方法中检查权限
const handleAdd = () => {
  if (!permissions.canCreate('user')) return
  // 执行新增逻辑
}

// 或使用装饰器
const handleDelete = requirePermission('user:delete', '删除用户')(() => {
  // 执行删除逻辑
})
```

### 权限缓存和刷新

```javascript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 手动刷新权限
const refreshPermissions = async () => {
  await authStore.refreshPermissions()
}

// 检查缓存状态
const isCacheValid = authStore.permissionCache.isValid()

// 清除缓存
authStore.permissionCache.clear()
```

## 权限配置规范

### 权限命名规范

```
模块:操作
```

**标准操作：**
- `list` - 查看列表
- `create` - 新增
- `update` - 编辑
- `delete` - 删除
- `import` - 导入
- `export` - 导出
- `audit` - 审核

**示例：**
```
user:list      # 查看用户列表
user:create    # 新增用户
user:update    # 编辑用户
user:delete    # 删除用户
project:audit  # 审核项目
```

### 角色配置

**标准角色：**
- `ADMIN` - 系统管理员
- `MANAGER` - 部门经理
- `LEADER` - 小组组长
- `MEMBER` - 普通成员

## 最佳实践

### 1. 权限设计原则

- **最小权限原则**：用户只获得完成工作所需的最小权限
- **职责分离**：不同角色具有不同的权限范围
- **权限继承**：上级角色继承下级角色的权限

### 2. 组件使用建议

- **页面入口**：使用 `PermissionCheck` 包装整个页面
- **操作按钮**：使用 `v-permission` 指令控制显示
- **复杂操作**：在方法中使用权限检查函数
- **用户提示**：使用 `PermissionTooltip` 提供友好提示

### 3. 性能优化

- **缓存利用**：合理使用30分钟权限缓存
- **懒加载**：权限验证通过后再加载页面组件
- **批量检查**：一次性检查多个权限，避免重复调用

### 4. 错误处理

- **友好提示**：权限不足时显示清晰的错误信息
- **降级处理**：部分权限缺失时提供基础功能
- **联系方式**：提供申请权限的联系方式

## 故障排查

### 常见问题

1. **权限不生效**
   - 检查用户是否已登录
   - 确认权限数据格式正确
   - 验证权限命名是否匹配

2. **菜单不显示**
   - 检查菜单配置中的权限设置
   - 确认用户具有相应权限
   - 验证菜单过滤逻辑

3. **路由被拦截**
   - 检查路由守卫配置
   - 确认路由元信息中的权限设置
   - 验证用户权限初始化状态

### 调试工具

```javascript
// 开启权限调试模式
localStorage.setItem('PERMISSION_DEBUG', 'true')

// 查看当前用户权限
console.log(authStore.userPermissions)

// 检查特定权限
console.log(authStore.hasPermission('user:list'))
```

## 更新日志

### v1.0.0
- ✅ 完整的权限控制系统
- ✅ iOS风格设计
- ✅ 多层级权限验证
- ✅ 权限缓存机制
- ✅ 通用CRUD模板
- ✅ 丰富的组件库

## 技术支持

如有问题或建议，请联系开发团队或提交Issue。

---

*本文档最后更新时间：2024年*