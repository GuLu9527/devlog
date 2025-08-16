package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TOperationLog;
import com.giao.devlogpulse.service.ITOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {

    @Autowired
    private ITOperationLogService operationLogService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<TOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(TOperationLog::getUserId, userId);
        }
        if (groupId != null) {
            queryWrapper.eq(TOperationLog::getGroupId, groupId);
        }
        if (module != null && !module.isEmpty()) {
            queryWrapper.eq(TOperationLog::getModule, module);
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(TOperationLog::getType, type);
        }
        if (status != null) {
            queryWrapper.eq(TOperationLog::getStatus, status);
        }
        if (startTime != null) {
            queryWrapper.ge(TOperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(TOperationLog::getOperationTime, endTime);
        }
        
        queryWrapper.orderByDesc(TOperationLog::getOperationTime);
        
        Page<TOperationLog> logPage = operationLogService.page(page, queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", logPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TOperationLog log = operationLogService.getById(id);
        if (log == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "操作日志不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", log);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        LambdaQueryWrapper<TOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        
        if (groupId != null) {
            queryWrapper.eq(TOperationLog::getGroupId, groupId);
        }
        if (startTime != null) {
            queryWrapper.ge(TOperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(TOperationLog::getOperationTime, endTime);
        }
        
        // 统计总操作次数
        long totalCount = operationLogService.count(queryWrapper);
        
        // 统计成功操作次数
        queryWrapper.eq(TOperationLog::getStatus, 1);
        long successCount = operationLogService.count(queryWrapper);
        
        // 统计失败操作次数
        queryWrapper.clear();
        if (groupId != null) {
            queryWrapper.eq(TOperationLog::getGroupId, groupId);
        }
        if (startTime != null) {
            queryWrapper.ge(TOperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(TOperationLog::getOperationTime, endTime);
        }
        queryWrapper.eq(TOperationLog::getStatus, 0);
        long failCount = operationLogService.count(queryWrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("successCount", successCount);
        statistics.put("failCount", failCount);
        statistics.put("successRate", totalCount > 0 ? (double) successCount / totalCount : 0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", statistics);
        return ResponseEntity.ok(response);
    }
} 