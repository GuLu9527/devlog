package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TGroup;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.service.ITGroupService;
import com.giao.devlogpulse.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 小组表 前端控制器
 * </p>
 *
 * @author giao
 * @since 2025-05-25
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private ITGroupService groupService;

    @Autowired
    private ITUserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TGroup group) {
        // 检查小组名称是否已存在
        if (groupService.lambdaQuery()
                .eq(TGroup::getName, group.getName())
                .exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NAME_EXISTS.getCode());
            response.put("message", ResultCode.GROUP_NAME_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查组长是否存在
        if (!userService.lambdaQuery()
                .eq(TUser::getId, group.getLeaderId())
                .exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.USER_NOT_FOUND.getCode());
            response.put("message", ResultCode.USER_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 设置默认状态为启用
        if (group.getStatus() == null) {
            group.setStatus(1);
        }
        
        group.setCreateTime(LocalDateTime.now());
        group.setUpdateTime(LocalDateTime.now());
        groupService.save(group);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody TGroup group) {
        // 检查小组是否存在
        TGroup existingGroup = groupService.getById(id);
        if (existingGroup == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NOT_FOUND.getCode());
            response.put("message", ResultCode.GROUP_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查小组名称是否与其他小组重复
        if (!existingGroup.getName().equals(group.getName()) &&
            groupService.lambdaQuery()
                .eq(TGroup::getName, group.getName())
                .exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NAME_EXISTS.getCode());
            response.put("message", ResultCode.GROUP_NAME_EXISTS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查组长是否存在
        if (!userService.lambdaQuery()
                .eq(TUser::getId, group.getLeaderId())
                .exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.USER_NOT_FOUND.getCode());
            response.put("message", ResultCode.USER_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        group.setId(id);
        group.setUpdateTime(LocalDateTime.now());
        groupService.updateById(group);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        // 检查小组是否存在
        TGroup group = groupService.getById(id);
        if (group == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NOT_FOUND.getCode());
            response.put("message", ResultCode.GROUP_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查小组下是否有用户
        if (userService.lambdaQuery()
                .eq(TUser::getGroupId, id)
                .exists()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_HAS_USERS.getCode());
            response.put("message", ResultCode.GROUP_HAS_USERS.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        groupService.removeById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TGroup group = groupService.getById(id);
        if (group == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NOT_FOUND.getCode());
            response.put("message", ResultCode.GROUP_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", group);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        List<TGroup> groups = groupService.list();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", groups);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long leaderId,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<TGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, TGroup::getName, name)
                   .eq(departmentId != null, TGroup::getDepartmentId, departmentId)
                   .eq(leaderId != null, TGroup::getLeaderId, leaderId)
                   .eq(status != null, TGroup::getStatus, status)
                   .orderByDesc(TGroup::getCreateTime);
        
        Page<TGroup> page = groupService.page(new Page<>(current, size), queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", page);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        // 检查小组是否存在
        TGroup group = groupService.getById(id);
        if (group == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.GROUP_NOT_FOUND.getCode());
            response.put("message", ResultCode.GROUP_NOT_FOUND.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        // 更新状态
        group.setStatus(status);
        group.setUpdateTime(LocalDateTime.now());
        groupService.updateById(group);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }
}
