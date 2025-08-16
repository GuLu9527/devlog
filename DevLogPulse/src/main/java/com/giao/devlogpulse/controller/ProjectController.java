package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TProject;
import com.giao.devlogpulse.service.ITProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@Tag(name = "项目管理", description = "项目管理相关接口")
public class ProjectController {

    @Autowired
    private ITProjectService projectService;

    @PostMapping
    @SaCheckPermission("project:create")
    @Operation(summary = "创建项目")
    public ResponseEntity<Map<String, Object>> create(@RequestBody TProject project) {
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        project.setIsDeleted(0);
        
        if (projectService.save(project)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            response.put("data", project);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", ResultCode.FAILED.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    @SaCheckPermission("project:list")
    @Operation(summary = "获取项目列表")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime) {
        
        Page<TProject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TProject> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(TProject::getIsDeleted, 0);
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(TProject::getName, name);
        }
        if (status != null && !status.isEmpty()) {
            // 支持多个状态查询，用逗号分隔
            if (status.contains(",")) {
                String[] statusArray = status.split(",");
                queryWrapper.in(TProject::getStatus, (Object[]) statusArray);
            } else {
                queryWrapper.eq(TProject::getStatus, status);
            }
        }
        if (managerId != null) {
            queryWrapper.eq(TProject::getManagerId, managerId);
        }
        if (departmentId != null) {
            queryWrapper.eq(TProject::getDepartmentId, departmentId);
        }
        if (startTime != null) {
            queryWrapper.ge(TProject::getStartTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(TProject::getEndTime, endTime);
        }
        
        queryWrapper.orderByDesc(TProject::getCreateTime);
        
        Page<TProject> projectPage = projectService.page(page, queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", projectPage);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有项目列表（不分页）
     */
    @GetMapping("/all")
    @SaCheckPermission("project:list:all")
    @Operation(summary = "获取所有项目")
    public Result<List<TProject>> listAll() {
        LambdaQueryWrapper<TProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TProject::getIsDeleted, 0)
                   .orderByDesc(TProject::getCreateTime);
        
        List<TProject> projectList = projectService.list(queryWrapper);
        return Result.success(projectList);
    }

    @GetMapping("/{id}")
    @SaCheckPermission("project:view")
    @Operation(summary = "获取项目详情")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        TProject project = projectService.getById(id);
        if (project == null || project.getIsDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "项目不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", project);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("project:update")
    @Operation(summary = "更新项目")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody TProject project) {
        project.setId(id);
        project.setUpdateTime(LocalDateTime.now());
        
        if (projectService.updateById(project)) {
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
    @SaCheckPermission("project:delete")
    @Operation(summary = "删除项目")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        TProject project = new TProject();
        project.setId(id);
        project.setIsDeleted(1);
        project.setUpdateTime(LocalDateTime.now());
        
        if (projectService.updateById(project)) {
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

    /**
     * 获取项目任务统计信息
     */
    @GetMapping("/{id}/stats")
    @SaCheckPermission("project:stats")
    @Operation(summary = "获取项目统计信息")
    public Result<Map<String, Object>> getProjectStats(@PathVariable Long id) {
        TProject project = projectService.getById(id);
        if (project == null || project.getIsDeleted() == 1) {
            return Result.failed("项目不存在");
        }
        
        // 获取真实的项目统计数据
        Map<String, Object> stats = projectService.getProjectStatistics(id);
        
        // 处理返回数据格式，确保前端需要的字段都存在
        Map<String, Object> result = new HashMap<>();
        result.put("totalTasks", stats.getOrDefault("totalTasks", 0));
        result.put("completedTasks", stats.getOrDefault("completedTasks", 0));
        result.put("inProgressTasks", stats.getOrDefault("inProgressTasks", 0));
        result.put("pendingTasks", stats.getOrDefault("pendingTasks", 0));
        result.put("overallProgress", stats.getOrDefault("progressPercentage", 0));
        result.put("highPriorityTasks", stats.getOrDefault("highPriorityTasks", 0));
        result.put("mediumPriorityTasks", stats.getOrDefault("mediumPriorityTasks", 0));
        result.put("lowPriorityTasks", stats.getOrDefault("lowPriorityTasks", 0));
        
        return Result.success(result);
    }

    /**
     * 获取项目进度趋势
     */
    @GetMapping("/{id}/progress-trend")
    @SaCheckPermission("project:trend")
    @Operation(summary = "获取项目进度趋势")
    public Result<Map<String, Object>> getProjectProgressTrend(@PathVariable Long id) {
        // 模拟趋势数据，实际应该从数据库查询
        Map<String, Object> trend = new HashMap<>();
        // 这里可以添加趋势数据的具体逻辑
        return Result.success(trend);
    }

    /**
     * 获取项目进度统计信息
     */
    @GetMapping("/{id}/progress")
    @SaCheckPermission("project:view")
    @Operation(summary = "获取项目进度信息")
    public ResponseEntity<Map<String, Object>> getProjectProgress(@PathVariable Long id) {
        TProject project = projectService.getById(id);
        if (project == null || project.getIsDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "项目不存在");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> progressData = projectService.getProjectStatistics(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", progressData);
        return ResponseEntity.ok(response);
    }

    /**
     * 手动触发项目进度更新
     */
    @PostMapping("/{id}/refresh-progress")
    @SaCheckPermission("project:update")
    @Operation(summary = "刷新项目进度")
    public ResponseEntity<Map<String, Object>> refreshProjectProgress(@PathVariable Long id) {
        TProject project = projectService.getById(id);
        if (project == null || project.getIsDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "项目不存在");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = projectService.updateProjectProgress(id);
        Map<String, Object> progressData = projectService.getProjectStatistics(id);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", "项目进度更新成功");
        } else {
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "项目进度更新失败");
        }
        response.put("data", progressData);
        return ResponseEntity.ok(response);
    }
} 