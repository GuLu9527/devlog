package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TRolePermission;
import com.giao.devlogpulse.service.ITRolePermissionService;
import com.giao.devlogpulse.service.ITRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-permissions")
public class RolePermissionController {

    @Autowired
    private ITRolePermissionService rolePermissionService;

    @Autowired
    private ITRoleService roleService;

    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignPermissions(
            @RequestParam Long roleId,
            @RequestBody List<Long> permissionIds) {
        
        // 检查角色是否存在
        TRole role = roleService.getById(roleId);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // 删除原有权限
        rolePermissionService.lambdaUpdate()
                .eq(TRolePermission::getRoleId, roleId)
                .remove();

        // 分配新权限
        for (Long permissionId : permissionIds) {
            TRolePermission rolePermission = new TRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreateTime(LocalDateTime.now());
            rolePermissionService.save(rolePermission);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<Map<String, Object>> getPermissionsByRoleId(@PathVariable Long roleId) {
        // 检查角色是否存在
        TRole role = roleService.getById(roleId);
        if (role == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.ROLE_NOT_FOUND.getCode());
            response.put("message", ResultCode.ROLE_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        List<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(roleId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", permissionIds);
        return ResponseEntity.ok(response);
    }
} 