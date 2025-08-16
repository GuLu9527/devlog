package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.Result;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TRolePermission;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统测试控制器 - 用于诊断权限问题
 * 
 * @author giao
 */
@RestController
@RequestMapping("/system-test")
@Slf4j
public class SystemTestController {

    @Autowired
    private ITUserService userService;
    
    @Autowired
    private ITRoleService roleService;
    
    @Autowired
    private ITPermissionService permissionService;
    
    @Autowired
    private ITRolePermissionService rolePermissionService;
    

    /**
     * 初始化基础权限数据
     */
    @GetMapping("/init-permissions")
    public Result<Map<String, Object>> initPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 创建基础权限
            createPermissionIfNotExists("user:list", "用户列表查看", "/users", "GET");
            createPermissionIfNotExists("user:create", "用户创建", "/users", "POST");
            createPermissionIfNotExists("user:update", "用户更新", "/users/*", "PUT");
            createPermissionIfNotExists("user:delete", "用户删除", "/users/*", "DELETE");
            
            createPermissionIfNotExists("department:list", "部门列表查看", "/departments", "GET");
            createPermissionIfNotExists("department:create", "部门创建", "/departments", "POST");
            createPermissionIfNotExists("department:update", "部门更新", "/departments/*", "PUT");
            createPermissionIfNotExists("department:delete", "部门删除", "/departments/*", "DELETE");
            
            createPermissionIfNotExists("task:list", "任务列表查看", "/tasks", "GET");
            createPermissionIfNotExists("task:create", "任务创建", "/tasks", "POST");
            createPermissionIfNotExists("task:update", "任务更新", "/tasks/*", "PUT");
            createPermissionIfNotExists("task:delete", "任务删除", "/tasks/*", "DELETE");
            
            createPermissionIfNotExists("worklog:review", "工作日志审核", "/worklogs/*/review", "PUT");
            
            createPermissionIfNotExists("notification:list", "通知列表查看", "/notifications/list", "GET");
            createPermissionIfNotExists("notification:read", "通知读取", "/notifications/unread-count", "GET");
            createPermissionIfNotExists("notification:update", "通知更新", "/notifications/*", "PUT");
            createPermissionIfNotExists("notification:mark-read", "标记通知已读", "/notifications/mark-read", "PUT");
            createPermissionIfNotExists("notification:mark-all-read", "标记所有通知已读", "/notifications/mark-all-read", "PUT");
            createPermissionIfNotExists("notification:delete", "删除通知", "/notifications/delete", "DELETE");
            createPermissionIfNotExists("notification:send", "发送通知", "/notifications/send", "POST");
            createPermissionIfNotExists("notification:batch-send", "批量发送通知", "/notifications/batch-send", "POST");
            
            // 2. 创建管理员角色
            TRole adminRole = roleService.lambdaQuery()
                .eq(TRole::getName, "管理员")
                .one();
            
            if (adminRole == null) {
                adminRole = new TRole();
                adminRole.setName("管理员");
                adminRole.setDescription("系统管理员，拥有所有权限");
                roleService.save(adminRole);
            }
            
            // 3. 为管理员角色分配所有权限
            List<TPermission> allPermissions = permissionService.list();
            for (TPermission permission : allPermissions) {
                TRolePermission existing = rolePermissionService.lambdaQuery()
                    .eq(TRolePermission::getRoleId, adminRole.getId())
                    .eq(TRolePermission::getPermissionId, permission.getId())
                    .one();
                
                if (existing == null) {
                    TRolePermission rolePermission = new TRolePermission();
                    rolePermission.setRoleId(adminRole.getId());
                    rolePermission.setPermissionId(permission.getId());
                    rolePermissionService.save(rolePermission);
                }
            }
            
            // 4. 确保admin用户有管理员角色
            TUser adminUser = userService.lambdaQuery()
                .eq(TUser::getUsername, "admin")
                .one();
            
            if (adminUser != null && !adminRole.getId().equals(adminUser.getRoleId())) {
                adminUser.setRoleId(adminRole.getId());
                userService.updateById(adminUser);
            }
            
            result.put("success", true);
            result.put("message", "权限数据初始化完成");
            result.put("adminRoleId", adminRole.getId());
            result.put("totalPermissions", allPermissions.size());
            
        } catch (Exception e) {
            log.error("初始化权限数据失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return Result.success(result);
    }
    
    private void createPermissionIfNotExists(String code, String name, String url, String method) {
        TPermission existing = permissionService.lambdaQuery()
            .eq(TPermission::getCode, code)
            .one();
        
        if (existing == null) {
            TPermission permission = new TPermission();
            permission.setCode(code);
            permission.setName(name);
            permission.setUrl(url);
            permission.setMethod(method);
            permission.setDescription(name);
            permissionService.save(permission);
        }
    }

    /**
     * 检查系统权限配置
     */
    @GetMapping("/permission-check")
    public Result<Map<String, Object>> checkPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 检查用户数据
            List<TUser> users = userService.list();
            result.put("userCount", users.size());
            
            TUser testUser = userService.lambdaQuery()
                .eq(TUser::getUsername, "admin")
                .one();
            
            if (testUser != null) {
                result.put("testUser", Map.of(
                    "id", testUser.getId(),
                    "username", testUser.getUsername(),
                    "roleId", testUser.getRoleId()
                ));
                
                // 2. 检查角色数据
                if (testUser.getRoleId() != null) {
                    TRole role = roleService.getById(testUser.getRoleId());
                    if (role != null) {
                        result.put("userRole", Map.of(
                            "id", role.getId(),
                            "name", role.getName()
                        ));
                        
                        // 3. 检查权限关联
                        List<TRolePermission> rolePermissions = rolePermissionService.lambdaQuery()
                            .eq(TRolePermission::getRoleId, role.getId())
                            .list();
                        result.put("rolePermissionCount", rolePermissions.size());
                        
                        // 4. 检查具体权限
                        List<TPermission> permissions = rolePermissionService.getPermissionsByRoleId(role.getId());
                        result.put("permissionCount", permissions.size());
                        result.put("permissions", permissions.stream()
                            .map(p -> Map.of("code", p.getCode(), "name", p.getName()))
                            .toArray());
                    }
                }
            } else {
                result.put("error", "admin用户不存在");
            }
            
            // 5. 检查所有权限
            List<TPermission> allPermissions = permissionService.list();
            result.put("allPermissionCount", allPermissions.size());
            result.put("hasUserListPermission", allPermissions.stream()
                .anyMatch(p -> "user:list".equals(p.getCode())));
            
        } catch (Exception e) {
            log.error("权限检查失败", e);
            result.put("error", e.getMessage());
        }
        
        return Result.success(result);
    }
    
    /**
     * 详细诊断权限问题
     */
    @GetMapping("/diagnose-permission/{username}")
    public Result<Map<String, Object>> diagnosePermission(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始诊断用户权限: {}", username);
            
            // 1. 查询用户
            TUser user = userService.lambdaQuery()
                .eq(TUser::getUsername, username)
                .one();
            
            if (user == null) {
                result.put("error", "用户不存在: " + username);
                return Result.success(result);
            }
            
            result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "roleId", user.getRoleId(),
                "status", user.getStatus()
            ));
            
            // 2. 检查角色
            if (user.getRoleId() == null) {
                result.put("error", "用户没有分配角色");
                return Result.success(result);
            }
            
            TRole role = roleService.getById(user.getRoleId());
            if (role == null) {
                result.put("error", "角色不存在: " + user.getRoleId());
                return Result.success(result);
            }
            
            result.put("role", Map.of(
                "id", role.getId(),
                "name", role.getName(),
                "description", role.getDescription()
            ));
            
            // 3. 检查角色权限关联
            List<TRolePermission> rolePermissions = rolePermissionService.lambdaQuery()
                .eq(TRolePermission::getRoleId, role.getId())
                .list();
            
            result.put("rolePermissionRelations", rolePermissions.size());
            result.put("rolePermissionDetails", rolePermissions.stream()
                .map(rp -> Map.of("roleId", rp.getRoleId(), "permissionId", rp.getPermissionId()))
                .toArray());
            
            // 4. 获取权限ID列表
            List<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(role.getId());
            result.put("permissionIdCount", permissionIds.size());
            result.put("permissionIds", permissionIds);
            
            // 5. 获取权限详情
            if (!permissionIds.isEmpty()) {
                List<TPermission> permissions = permissionService.listByIds(permissionIds);
                result.put("permissionCount", permissions.size());
                result.put("permissions", permissions.stream()
                    .map(p -> Map.of(
                        "id", p.getId(),
                        "code", p.getCode(),
                        "name", p.getName(),
                        "url", p.getUrl(),
                        "method", p.getMethod()
                    ))
                    .toArray());
                
                // 6. 特别检查user:list权限
                boolean hasUserListPermission = permissions.stream()
                    .anyMatch(p -> "user:list".equals(p.getCode()));
                result.put("hasUserListPermission", hasUserListPermission);
                
                if (!hasUserListPermission) {
                    // 检查是否存在user:list权限
                    TPermission userListPermission = permissionService.lambdaQuery()
                        .eq(TPermission::getCode, "user:list")
                        .one();
                    
                    if (userListPermission == null) {
                        result.put("userListPermissionStatus", "权限不存在，需要创建");
                    } else {
                        result.put("userListPermissionStatus", "权限存在但未分配给角色");
                        result.put("userListPermissionId", userListPermission.getId());
                        
                        // 检查关联关系是否存在
                        boolean relationExists = rolePermissionService.lambdaQuery()
                            .eq(TRolePermission::getRoleId, role.getId())
                            .eq(TRolePermission::getPermissionId, userListPermission.getId())
                            .exists();
                        result.put("userListRelationExists", relationExists);
                    }
                }
            } else {
                result.put("error", "角色没有分配任何权限");
            }
            
            log.info("权限诊断完成: {}", result);
            
        } catch (Exception e) {
            log.error("权限诊断失败", e);
            result.put("error", "诊断过程中发生异常: " + e.getMessage());
            result.put("exception", e.getClass().getSimpleName());
        }
        
        return Result.success(result);
    }

    /**
     * 修复管理员权限
     */
    @GetMapping("/fix-admin-permission")
    public Result<Map<String, Object>> fixAdminPermission() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始修复管理员权限");
            
            // 1. 确保admin用户存在
            TUser adminUser = userService.lambdaQuery()
                .eq(TUser::getUsername, "admin")
                .one();
            
            if (adminUser == null) {
                result.put("error", "admin用户不存在，需要先创建admin用户");
                return Result.success(result);
            }
            
            // 2. 确保管理员角色存在
            TRole adminRole = roleService.lambdaQuery()
                .eq(TRole::getName, "管理员")
                .one();
            
            if (adminRole == null) {
                adminRole = new TRole();
                adminRole.setName("管理员");
                adminRole.setDescription("系统管理员，拥有所有权限");
                roleService.save(adminRole);
                log.info("创建管理员角色: {}", adminRole.getId());
            }
            
            // 3. 确保admin用户有管理员角色
            if (!adminRole.getId().equals(adminUser.getRoleId())) {
                adminUser.setRoleId(adminRole.getId());
                userService.updateById(adminUser);
                log.info("为admin用户分配管理员角色");
            }
            
            // 4. 获取所有权限
            List<TPermission> allPermissions = permissionService.list();
            log.info("系统总权限数: {}", allPermissions.size());
            
            // 5. 为管理员角色分配所有权限
            int assignedCount = 0;
            for (TPermission permission : allPermissions) {
                boolean exists = rolePermissionService.lambdaQuery()
                    .eq(TRolePermission::getRoleId, adminRole.getId())
                    .eq(TRolePermission::getPermissionId, permission.getId())
                    .exists();
                
                if (!exists) {
                    TRolePermission rolePermission = new TRolePermission();
                    rolePermission.setRoleId(adminRole.getId());
                    rolePermission.setPermissionId(permission.getId());
                    rolePermissionService.save(rolePermission);
                    assignedCount++;
                    log.info("为管理员角色分配权限: {} - {}", permission.getCode(), permission.getName());
                }
            }
            
            result.put("success", true);
            result.put("adminUserId", adminUser.getId());
            result.put("adminRoleId", adminRole.getId());
            result.put("totalPermissions", allPermissions.size());
            result.put("newlyAssignedPermissions", assignedCount);
            result.put("message", "管理员权限修复完成");
            
            log.info("管理员权限修复完成，新分配了 {} 个权限", assignedCount);
            
        } catch (Exception e) {
            log.error("修复管理员权限失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return Result.success(result);
    }

    /**
     * 检查token传输情况
     */
    @GetMapping("/check-token")
    public Result<Map<String, Object>> checkToken(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查Authorization header
        String authHeader = request.getHeader("Authorization");
        result.put("authorizationHeader", authHeader);
        
        // 检查request中的属性
        Object userId = request.getAttribute("userId");
        Object username = request.getAttribute("username");
        
        result.put("requestUserId", userId);
        result.put("requestUsername", username);
        
        // 检查所有headers
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            headers.put(name, request.getHeader(name));
        });
        result.put("allHeaders", headers);
        
        // 检查SA-Token解析情况
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            result.put("tokenLength", token.length());
            result.put("tokenPrefix", token.substring(0, Math.min(20, token.length())) + "...");
            
            // 检查token是否能正确解析
            try {
                // 使用SA-Token检查登录状态
                boolean isLogin = StpUtil.isLogin();
                result.put("isLogin", isLogin);
                
                if (isLogin) {
                    Long userIdFromToken = StpUtil.getLoginIdAsLong();
                    result.put("tokenUserId", userIdFromToken);
                    result.put("tokenStatus", "SA-Token验证成功");
                } else {
                    result.put("tokenStatus", "SA-Token未登录");
                }
            } catch (Exception e) {
                result.put("tokenParseError", e.getMessage());
                result.put("tokenStatus", "SA-Token解析失败");
            }
        }
        
        log.info("Token检查结果: {}", result);
        return Result.success(result);
    }

    /**
     * 模拟通知接口（用于测试403问题）
     */
    @GetMapping("/mock-notification")
    public Result<Map<String, Object>> mockNotification(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("=== 模拟通知接口被调用 ===");
        log.info("请求URI: {}", request.getRequestURI());
        
        // 检查request中的用户信息
        String username = (String) request.getAttribute("username");
        Long userId = StpUtil.getLoginIdAsLong();
        
        log.info("从request获取的username: {}", username);
        log.info("从request获取的userId: {}", userId);
        
        result.put("username", username);
        result.put("userId", userId);
        result.put("message", "模拟通知接口调用成功");
        result.put("timestamp", System.currentTimeMillis());
        
        if (userId == null) {
            result.put("error", "用户ID为空，可能是token问题");
        }
        
        return Result.success(result);
    }

    /**
     * 快速修复权限问题
     */
    @GetMapping("/quick-fix-permissions")
    public Result<Map<String, Object>> quickFixPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始快速修复权限问题");
            
            // 1. 确保所有关键权限存在
            createPermissionIfNotExists("user:read", "用户读取", "/users", "GET");
            createPermissionIfNotExists("notification:read", "通知读取", "/notifications/unread-count", "GET");
            
            // 2. 确保admin用户存在并有管理员角色
            TUser adminUser = userService.lambdaQuery()
                .eq(TUser::getUsername, "admin")
                .one();
            
            if (adminUser == null) {
                result.put("error", "admin用户不存在");
                return Result.success(result);
            }
            
            // 3. 确保管理员角色存在
            TRole adminRole = roleService.lambdaQuery()
                .eq(TRole::getName, "管理员")
                .one();
            
            if (adminRole == null) {
                adminRole = new TRole();
                adminRole.setName("管理员");
                adminRole.setDescription("系统管理员");
                roleService.save(adminRole);
            }
            
            // 4. 确保admin用户有管理员角色
            if (!adminRole.getId().equals(adminUser.getRoleId())) {
                adminUser.setRoleId(adminRole.getId());
                userService.updateById(adminUser);
            }
            
            // 5. 为管理员角色分配所有权限
            List<TPermission> allPermissions = permissionService.list();
            int addedCount = 0;
            
            for (TPermission permission : allPermissions) {
                boolean exists = rolePermissionService.lambdaQuery()
                    .eq(TRolePermission::getRoleId, adminRole.getId())
                    .eq(TRolePermission::getPermissionId, permission.getId())
                    .exists();
                
                if (!exists) {
                    TRolePermission rolePermission = new TRolePermission();
                    rolePermission.setRoleId(adminRole.getId());
                    rolePermission.setPermissionId(permission.getId());
                    rolePermissionService.save(rolePermission);
                    addedCount++;
                }
            }
            
            // 6. 验证notification:read权限
            TPermission notificationReadPermission = permissionService.lambdaQuery()
                .eq(TPermission::getCode, "notification:read")
                .one();
            
            boolean hasNotificationRead = false;
            if (notificationReadPermission != null) {
                hasNotificationRead = rolePermissionService.lambdaQuery()
                    .eq(TRolePermission::getRoleId, adminRole.getId())
                    .eq(TRolePermission::getPermissionId, notificationReadPermission.getId())
                    .exists();
            }
            
            result.put("success", true);
            result.put("adminUserId", adminUser.getId());
            result.put("adminRoleId", adminRole.getId());
            result.put("totalPermissions", allPermissions.size());
            result.put("addedPermissions", addedCount);
            result.put("hasNotificationReadPermission", hasNotificationRead);
            result.put("notificationReadPermissionId", notificationReadPermission != null ? notificationReadPermission.getId() : null);
            
            log.info("权限修复完成，新增 {} 个权限关联", addedCount);
            
        } catch (Exception e) {
            log.error("权限修复失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return Result.success(result);
    }

    /**
     * 测试权限验证是否工作（无需权限）
     */
    @GetMapping("/test-auth")
    public Result<Map<String, Object>> testAuth() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "这个接口不需要权限验证");
        result.put("timestamp", System.currentTimeMillis());
        return Result.success(result);
    }

    /**
     * 测试权限验证 - 需要user:read权限
     */
    @GetMapping("/test-permission")
    @SaCheckPermission("user:read")
    public Result<Map<String, Object>> testPermissionRequired(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("=== 权限验证测试接口被调用 ===");
        
        try {
            // 获取当前用户信息
            Long currentUserId = StpUtil.getLoginIdAsLong();
            String currentUsername = (String) request.getAttribute("username");
            
            result.put("currentUserId", currentUserId);
            result.put("currentUsername", currentUsername);
            result.put("message", "权限验证成功！您有user:read权限");
            result.put("timestamp", System.currentTimeMillis());
            
            // 检查Authorization头
            String authorizationHeader = request.getHeader("Authorization");
            result.put("hasAuthHeader", authorizationHeader != null);
            
            if (currentUserId != null) {
                TUser user = userService.getById(currentUserId);
                if (user != null) {
                    result.put("currentUserName", user.getUsername());
                    result.put("currentUserRole", user.getRoleId());
                }
            }
            
            log.info("权限验证测试成功: {}", result);
            
        } catch (Exception e) {
            log.error("权限验证测试失败", e);
            result.put("error", e.getMessage());
        }
        
        return Result.success(result);
    }

    /**
     * 测试AOP是否工作
     */
    @GetMapping("/test-aop")
    public Result<String> testAOP() {
        log.info("🎯 SystemTestController.testAOP 被直接调用");
        return Result.success("AOP测试方法执行成功");
    }

    /**
     * 测试通知权限 - 需要notification:read权限
     */
    @GetMapping("/test-notification-permission")
    @SaCheckPermission("notification:read")
    public Result<Map<String, Object>> testNotificationPermission(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        log.info("=== 通知权限验证测试接口被调用 ===");

        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            String currentUsername = (String) request.getAttribute("username");
            
            result.put("currentUserId", currentUserId);
            result.put("currentUsername", currentUsername);
            result.put("message", "通知权限验证成功！您有notification:read权限");
            result.put("timestamp", System.currentTimeMillis());

            String authorizationHeader = request.getHeader("Authorization");
            result.put("hasAuthHeader", authorizationHeader != null);

            log.info("通知权限验证测试成功: {}", result);

        } catch (Exception e) {
            log.error("通知权限验证测试失败", e);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }
}