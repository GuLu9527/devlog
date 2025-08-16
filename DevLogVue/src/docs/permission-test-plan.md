# DevLog系统权限测试验证方案

## 一、测试目标

确保MEMBER角色用户只能看到以下菜单项：
- ✅ 首页
- ✅ 项目管理（项目列表、任务管理）
- ✅ 工作日志
- ✅ 通知消息

确保MEMBER角色用户**不能看到**以下菜单项：
- ❌ 系统管理（用户管理、角色管理、权限管理）
- ❌ 组织管理（部门管理、小组管理）

## 二、测试环境准备

### 2.1 数据库权限配置验证

执行以下SQL确认MEMBER角色权限：

```sql
-- 查看MEMBER角色的权限
SELECT 
    r.name as '角色名称',
    p.name as '权限名称',
    p.code as '权限编码',
    p.description as '权限描述'
FROM t_role r
JOIN t_role_permission rp ON r.id = rp.role_id
JOIN t_permission p ON rp.permission_id = p.id
WHERE r.id = 4 -- MEMBER角色ID
ORDER BY p.id;
```

预期结果：MEMBER应该只有以下权限
- project:list
- task:list
- worklog:list
- notification:list
- statistics:personal

### 2.2 测试用户创建

创建或确认存在MEMBER角色的测试用户：

```sql
-- 查找MEMBER角色的用户
SELECT 
    u.id,
    u.username,
    u.real_name,
    r.name as role_name
FROM t_user u
JOIN t_user_role ur ON u.id = ur.user_id
JOIN t_role r ON ur.role_id = r.id
WHERE r.id = 4;
```

## 三、测试步骤

### 3.1 前端权限测试

#### 步骤1：清理环境
1. 清除浏览器localStorage
2. 确保未登录状态

#### 步骤2：使用MEMBER用户登录
1. 使用MEMBER角色用户账号登录
2. 观察登录成功后的页面

#### 步骤3：菜单显示验证
1. 检查左侧菜单栏显示内容
2. 确认只显示允许的菜单项
3. 确认管理类菜单完全隐藏

#### 步骤4：权限调试页面检查
1. 访问 `/debug/permissions` 页面
2. 查看权限分析结果
3. 确认MEMBER权限验证通过

### 3.2 浏览器开发工具检查

#### 步骤1：Console日志检查
打开浏览器开发工具，查看Console中的权限调试信息：

```
[权限调试] SYSTEM_INIT: 权限系统初始化
[权限调试] MENU_ANALYSIS: 开始分析菜单权限配置
菜单权限检查: 首页(home) - 需要权限: null - 检查结果: true
菜单权限检查: 系统管理(system) - 需要权限: ["user:list","role:list","permission:list"] - 检查结果: false
菜单权限检查: 组织管理(organization) - 需要权限: ["department:list","group:list"] - 检查结果: false
菜单权限检查: 项目管理(project) - 需要权限: ["project:list","task:list"] - 检查结果: true
菜单权限检查: 工作日志(worklog) - 需要权限: "worklog:list" - 检查结果: true
菜单权限检查: 通知消息(notification) - 需要权限: "notification:list" - 检查结果: true
```

#### 步骤2：权限对象检查
在Console中执行：

```javascript
// 检查权限调试器
permissionDebugger.exportToConsole()

// 检查authStore状态
const authStore = window.$pinia.state.value.auth
console.log('用户权限:', authStore.permissions)
console.log('用户角色:', authStore.user?.roleName)
```

### 3.3 功能访问测试

#### 步骤1：直接访问测试
尝试直接访问管理页面URL：
- `/user` - 应该被重定向或显示403错误
- `/role` - 应该被重定向或显示403错误
- `/permission` - 应该被重定向或显示403错误
- `/department` - 应该被重定向或显示403错误
- `/group` - 应该被重定向或显示403错误

#### 步骤2：API请求测试
使用浏览器Network工具监控API请求，确保：
- 管理相关API不会被调用
- 权限验证在后端也生效

## 四、预期结果

### 4.1 菜单显示结果

✅ **正确显示的菜单**：
```
├── 首页
├── 项目管理
│   ├── 项目列表
│   └── 任务管理
├── 工作日志
└── 通知消息
```

❌ **不应显示的菜单**：
- 系统管理（整个分组）
- 组织管理（整个分组）

### 4.2 权限调试结果

权限分析应显示：
- 总菜单数：6（包含子菜单）
- 可访问：4个（首页、项目管理、工作日志、通知消息）
- 被拒绝：2个（系统管理、组织管理）

MEMBER权限验证应显示：
- ✅ 权限配置正确
- ✅ 拥有基本权限：project:list, task:list, worklog:list, notification:list
- ❌ 无意外的管理权限

## 五、异常情况处理

### 5.1 权限未生效的可能原因

1. **数据库权限配置错误**
   - 检查 strict_role_permission_assignment.sql 是否正确执行
   - 确认MEMBER角色的权限分配

2. **前端缓存问题**
   - 清除localStorage
   - 清除浏览器缓存
   - 硬刷新页面

3. **权限初始化问题**
   - 检查登录API返回的权限数据
   - 确认authStore初始化顺序

4. **菜单配置问题**
   - 检查menu.js中的权限配置
   - 确认menuUtils过滤逻辑

### 5.2 问题排查步骤

1. **数据库层排查**
   ```sql
   -- 检查角色权限关联
   SELECT COUNT(*) FROM t_role_permission WHERE role_id = 4;
   
   -- 检查用户角色关联
   SELECT * FROM t_user_role WHERE user_id = ?;
   ```

2. **API层排查**
   - 检查登录接口返回的权限数组
   - 确认权限编码格式正确

3. **前端层排查**
   - 在权限调试页面查看详细信息
   - 使用Console查看权限检查日志
   - 检查菜单过滤结果

## 六、验收标准

### 6.1 功能验收
- [ ] MEMBER用户登录后只看到4个允许的菜单项
- [ ] 系统管理和组织管理菜单完全不显示
- [ ] 直接访问管理页面URL被正确拦截
- [ ] 权限调试页面显示正确的分析结果

### 6.2 技术验收
- [ ] Console无权限相关错误日志
- [ ] 权限检查逻辑运行正常
- [ ] 菜单过滤性能良好
- [ ] 权限缓存机制正常工作

### 6.3 用户体验验收
- [ ] 菜单显示无闪烁或延迟
- [ ] 权限验证透明对用户
- [ ] 无权限时的反馈清晰
- [ ] 页面加载性能正常

## 七、回归测试

确保修复不影响其他角色：

1. **ADMIN角色测试**
   - 应该看到所有菜单项
   - 所有功能正常访问

2. **MANAGER角色测试**
   - 应该看到部分管理菜单
   - 权限范围符合配置

3. **LEADER角色测试**
   - 应该看到项目相关菜单
   - 权限范围符合配置

## 八、性能测试

1. **菜单渲染性能**
   - 测量菜单过滤耗时
   - 确保在可接受范围内（<50ms）

2. **权限检查性能**
   - 测量单次权限检查耗时
   - 确保批量检查性能

3. **内存使用**
   - 监控权限数据内存占用
   - 确保缓存大小合理