package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.service.ITPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private ITPermissionService permissionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TPermission permission) {
        // 检查权限编码是否已存在
        if (permissionService.lambdaQuery().eq(TPermission::getCode, permission.getCode()).exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.PERMISSION_CODE_EXISTS.getCode());
            response.put("message", ResultCode.PERMISSION_CODE_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        permission.setCreateTime(LocalDateTime.now());

        if (permissionService.save(permission)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            response.put("data", permission);
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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String method) {
        
        Page<TPermission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TPermission> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(TPermission::getName, name);
        }
        if (code != null && !code.isEmpty()) {
            queryWrapper.like(TPermission::getCode, code);
        }
        if (url != null && !url.isEmpty()) {
            queryWrapper.like(TPermission::getUrl, url);
        }
        if (method != null && !method.isEmpty()) {
            queryWrapper.eq(TPermission::getMethod, method);
        }
        
        queryWrapper.orderByDesc(TPermission::getCreateTime);
        
        Page<TPermission> permissionPage = permissionService.page(page, queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", permissionPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TPermission permission = permissionService.getById(id);
        if (permission == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.PERMISSION_NOT_FOUND.getCode());
            response.put("message", ResultCode.PERMISSION_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", permission);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody TPermission permission) {
        TPermission existingPermission = permissionService.getById(id);
        if (existingPermission == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.PERMISSION_NOT_FOUND.getCode());
            response.put("message", ResultCode.PERMISSION_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查权限编码是否已被其他权限使用
        if (!existingPermission.getCode().equals(permission.getCode()) &&
                permissionService.lambdaQuery().eq(TPermission::getCode, permission.getCode()).exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.PERMISSION_CODE_EXISTS.getCode());
            response.put("message", ResultCode.PERMISSION_CODE_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        permission.setId(id);
        
        if (permissionService.updateById(permission)) {
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
        // 检查权限是否存在
        TPermission permission = permissionService.getById(id);
        if (permission == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.PERMISSION_NOT_FOUND.getCode());
            response.put("message", ResultCode.PERMISSION_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查权限是否被角色使用
        if (permissionService.isUsedByRole(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "权限已被角色使用，无法删除");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (permissionService.removeById(id)) {
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
} 