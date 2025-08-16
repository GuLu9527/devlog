package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TLogTemplate;
import com.giao.devlogpulse.service.ITLogTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/log-templates")
public class LogTemplateController {

    @Autowired
    private ITLogTemplateService logTemplateService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TLogTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        
        if (logTemplateService.save(template)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            response.put("data", template);
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
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean isDefault) {
        
        Page<TLogTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TLogTemplate> queryWrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            queryWrapper.like(TLogTemplate::getTitle, title);
        }
        if (departmentId != null) {
            queryWrapper.eq(TLogTemplate::getDepartmentId, departmentId);
        }
        if (isDefault != null) {
            queryWrapper.eq(TLogTemplate::getIsDefault, isDefault ? 1 : 0);
        }
        
        queryWrapper.orderByDesc(TLogTemplate::getCreateTime);
        
        Page<TLogTemplate> templatePage = logTemplateService.page(page, queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", templatePage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TLogTemplate template = logTemplateService.getById(id);
        if (template == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "日志模板不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", template);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody TLogTemplate template) {
        template.setId(id);
        template.setUpdateTime(LocalDateTime.now());
        
        if (logTemplateService.updateById(template)) {
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
        if (logTemplateService.removeById(id)) {
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