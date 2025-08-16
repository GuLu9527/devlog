package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.service.ITRoleService;
import com.giao.devlogpulse.service.ITUserService;
import com.giao.devlogpulse.service.ITRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private ITRoleService roleService;

    @Autowired
    private ITUserService userService;

    @Autowired
    private ITRolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TRole role) {
        // 检查角色名称是否已存在
        if (roleService.lambdaQuery().eq(TRole::getName, role.getName()).exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NAME_EXISTS.getCode());
            response.put("message", ResultCode.ROLE_NAME_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());

        if (roleService.save(role)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            response.put("data", role);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", ResultCode.FAILED.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        
        Page<TRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TRole> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(TRole::getName, name);
        }
        
        queryWrapper.orderByDesc(TRole::getCreateTime);
        
        Page<TRole> rolePage = roleService.page(page, queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", rolePage);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<TRole> roles = roleService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", roles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TRole role = roleService.getById(id);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", role);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody TRole role) {
        TRole existingRole = roleService.getById(role.getId());
        if (existingRole == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查角色名称是否已被其他角色使用
        if (!existingRole.getName().equals(role.getName()) &&
                roleService.lambdaQuery().eq(TRole::getName, role.getName()).exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NAME_EXISTS.getCode());
            response.put("message", ResultCode.ROLE_NAME_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        role.setId(role.getId());
        role.setUpdateTime(LocalDateTime.now());
        
        if (roleService.updateById(role)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", ResultCode.FAILED.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        // 检查角色是否存在
        TRole role = roleService.getById(id);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查角色下是否有用户
        if (roleService.hasUsers(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "角色下存在用户，无法删除");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (roleService.removeById(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", ResultCode.FAILED.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users/{roleName}")
    public ResponseEntity<Map<String, Object>> getUsersByRoleName(@PathVariable String roleName) {
        // 1. 根据角色名称查询角色
        TRole role = roleService.lambdaQuery()
                .eq(TRole::getName, roleName)
                .one();
        
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 2. 根据角色ID查询用户列表
        List<TUser> users = userService.lambdaQuery()
                .eq(TUser::getRoleId, role.getId())
                .list();

        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取角色的权限列表
     * @param id 角色ID
     * @return 权限列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> getPermissionsByRoleId(@PathVariable Long id) {
        // 检查角色是否存在
        TRole role = roleService.getById(id);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 获取角色的权限列表
        List<TPermission> permissions = rolePermissionService.getPermissionsByRoleId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", permissions);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取角色的权限ID列表
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permission-ids")
    public ResponseEntity<Map<String, Object>> getPermissionIdsByRoleId(@PathVariable Long id) {
        // 检查角色是否存在
        TRole role = roleService.getById(id);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 获取角色的权限ID列表
        List<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", permissionIds);
        return ResponseEntity.ok(response);
    }

    /**
     * 为角色分配权限
     * @param id 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        
        // 检查角色是否存在
        TRole role = roleService.getById(id);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 调用service进行权限分配
            rolePermissionService.assignPermissionsToRole(id, permissionIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", "权限分配成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "权限分配失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 