package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.entity.po.TTaskDependency;
import com.giao.devlogpulse.service.ITTaskDependencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务依赖关系管理控制器
 */
@RestController
@RequestMapping("/task-dependencies")
@Tag(name = "任务依赖关系管理", description = "任务依赖关系相关接口")
public class TaskDependencyController {

    @Autowired
    private ITTaskDependencyService taskDependencyService;

    /**
     * 获取任务依赖关系
     */
    @GetMapping("/{taskId}")
    @SaCheckPermission("task:dependency:view")
    @Operation(summary = "获取任务依赖关系")
    public Result<Map<String, Object>> getTaskDependencies(@PathVariable Long taskId) {
        Map<String, Object> dependencies = taskDependencyService.getTaskDependencies(taskId);
        return Result.success(dependencies);
    }

    /**
     * 添加任务依赖关系
     */
    @PostMapping
    @SaCheckPermission("task:dependency:create")
    @Operation(summary = "添加任务依赖关系")
    public Result<Void> addTaskDependency(@RequestBody AddDependencyRequest request) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        boolean success = taskDependencyService.addTaskDependency(
            request.getTaskId(),
            request.getDependentTaskId(),
            request.getType(),
            request.getDescription(),
            currentUserId
        );
        
        return success ? Result.success() : Result.failed("添加依赖关系失败");
    }

    /**
     * 删除任务依赖关系
     */
    @DeleteMapping("/{taskId}/{dependentTaskId}")
    @SaCheckPermission("task:dependency:delete")
    @Operation(summary = "删除任务依赖关系")
    public Result<Void> removeTaskDependency(@PathVariable Long taskId, @PathVariable Long dependentTaskId) {
        boolean success = taskDependencyService.removeTaskDependency(taskId, dependentTaskId);
        return success ? Result.success() : Result.failed("删除依赖关系失败");
    }

    /**
     * 检查依赖冲突
     */
    @PostMapping("/check-conflict")
    @SaCheckPermission("task:dependency:view")
    @Operation(summary = "检查依赖冲突")
    public Result<Map<String, Object>> checkDependencyConflict(@RequestBody CheckConflictRequest request) {
        boolean hasConflict = taskDependencyService.checkCircularDependency(request.getTaskId(), request.getDependentTaskId());
        
        Map<String, Object> result = Map.of(
            "hasConflict", hasConflict,
            "message", hasConflict ? "存在循环依赖" : "无冲突"
        );
        
        return Result.success(result);
    }

    /**
     * 获取任务依赖路径
     */
    @GetMapping("/{taskId}/path")
    @SaCheckPermission("task:dependency:view")
    @Operation(summary = "获取任务依赖路径")
    public Result<Object> getTaskDependencyPath(@PathVariable Long taskId) {
        var path = taskDependencyService.getTaskDependencyPath(taskId);
        return Result.success(path);
    }

    /**
     * 添加依赖关系请求体
     */
    public static class AddDependencyRequest {
        private Long taskId;
        private Long dependentTaskId;
        private TTaskDependency.DependencyType type;
        private String description;

        // Getters and Setters
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        
        public Long getDependentTaskId() { return dependentTaskId; }
        public void setDependentTaskId(Long dependentTaskId) { this.dependentTaskId = dependentTaskId; }
        
        public TTaskDependency.DependencyType getType() { return type; }
        public void setType(TTaskDependency.DependencyType type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 检查冲突请求体
     */
    public static class CheckConflictRequest {
        private Long taskId;
        private Long dependentTaskId;

        // Getters and Setters
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        
        public Long getDependentTaskId() { return dependentTaskId; }
        public void setDependentTaskId(Long dependentTaskId) { this.dependentTaskId = dependentTaskId; }
    }
}