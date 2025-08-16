package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.entity.po.TTaskTemplate;
import com.giao.devlogpulse.service.ITTaskTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务模板管理控制器
 */
@RestController
@RequestMapping("/task-templates")
@Tag(name = "任务模板管理", description = "任务模板相关接口")
public class TaskTemplateController {

    @Autowired
    private ITTaskTemplateService taskTemplateService;

    /**
     * 获取任务模板列表
     */
    @GetMapping
    @Operation(summary = "获取任务模板列表")
    public Result<Page<TTaskTemplate>> getTemplateList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        try {
            Page<TTaskTemplate> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TTaskTemplate> queryWrapper = new LambdaQueryWrapper<>();
            
            queryWrapper.eq(TTaskTemplate::getIsDeleted, 0);
            
            if (category != null && !category.isEmpty()) {
                queryWrapper.eq(TTaskTemplate::getCategory, category);
            }
            
            if (keyword != null && !keyword.isEmpty()) {
                queryWrapper.and(wrapper -> wrapper.like(TTaskTemplate::getName, keyword)
                                                  .or()
                                                  .like(TTaskTemplate::getDescription, keyword));
            }
            
            queryWrapper.orderByDesc(TTaskTemplate::getUsageCount)
                       .orderByDesc(TTaskTemplate::getCreateTime);
            
            Page<TTaskTemplate> templatePage = taskTemplateService.page(page, queryWrapper);
            return Result.success(templatePage);
        } catch (Exception e) {
            return Result.failed("获取模板列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("task:template:view")
    @Operation(summary = "获取模板详情")
    public Result<TTaskTemplate> getTemplateById(@PathVariable Long id) {
        TTaskTemplate template = taskTemplateService.getById(id);
        if (template == null || template.getIsDeleted() == 1) {
            return Result.failed("模板不存在");
        }
        return Result.success(template);
    }

    /**
     * 创建任务模板
     */
    @PostMapping
    @SaCheckPermission("task:template:create")
    @Operation(summary = "创建任务模板")
    public Result<TTaskTemplate> createTemplate(@RequestBody TTaskTemplate template) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        template.setCreatorId(currentUserId);
        
        boolean success = taskTemplateService.createTemplate(template);
        return success ? Result.success(template) : Result.failed("创建模板失败");
    }

    /**
     * 更新任务模板
     */
    @PutMapping("/{id}")
    @SaCheckPermission("task:template:update")
    @Operation(summary = "更新任务模板")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody TTaskTemplate template) {
        TTaskTemplate existingTemplate = taskTemplateService.getById(id);
        if (existingTemplate == null || existingTemplate.getIsDeleted() == 1) {
            return Result.failed("模板不存在");
        }
        
        template.setId(id);
        template.setUpdateTime(LocalDateTime.now());
        
        // 重新计算任务数量和预计工时
        if (template.getTasks() != null && !template.getTasks().isEmpty()) {
            template.setTasksCount(template.getTasks().size());
            template.setEstimatedHours(template.getTasks().stream()
                    .map(TTaskTemplate.TaskTemplateItem::getEstimatedHours)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        }
        
        boolean success = taskTemplateService.updateById(template);
        return success ? Result.success() : Result.failed("更新模板失败");
    }

    /**
     * 删除任务模板
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("task:template:delete")
    @Operation(summary = "删除任务模板")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        TTaskTemplate template = taskTemplateService.getById(id);
        if (template == null || template.getIsDeleted() == 1) {
            return Result.failed("模板不存在");
        }
        
        template.setIsDeleted(1);
        template.setUpdateTime(LocalDateTime.now());
        
        boolean success = taskTemplateService.updateById(template);
        return success ? Result.success() : Result.failed("删除模板失败");
    }

    /**
     * 应用任务模板
     */
    @PostMapping("/{id}/apply")
    @SaCheckPermission("task:template:apply")
    @Operation(summary = "应用任务模板")
    public Result<Map<String, Object>> applyTemplate(@PathVariable Long id, @RequestBody ApplyTemplateRequest request) {
        try {
            List<Long> createdTaskIds = taskTemplateService.applyTemplate(
                id,
                request.getProjectId(),
                request.getAssigneeId(),
                request.getStartDate(),
                request.getTaskPrefix()
            );
            
            Map<String, Object> result = Map.of(
                "createdTaskIds", createdTaskIds,
                "createdCount", createdTaskIds.size(),
                "message", "模板应用成功，创建了 " + createdTaskIds.size() + " 个任务"
            );
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("应用模板失败：" + e.getMessage());
        }
    }

    /**
     * 克隆任务模板
     */
    @PostMapping("/{id}/clone")
    @SaCheckPermission("task:template:create")
    @Operation(summary = "克隆任务模板")
    public Result<TTaskTemplate> cloneTemplate(@PathVariable Long id, @RequestBody CloneTemplateRequest request) {
        try {
            TTaskTemplate clonedTemplate = taskTemplateService.cloneTemplate(id, request.getNewName());
            return Result.success(clonedTemplate);
        } catch (Exception e) {
            return Result.failed("克隆模板失败：" + e.getMessage());
        }
    }

    /**
     * 按分类获取模板
     */
    @GetMapping("/category/{category}")
    @SaCheckPermission("task:template:list")
    @Operation(summary = "按分类获取模板")
    public Result<List<TTaskTemplate>> getTemplatesByCategory(@PathVariable String category) {
        List<TTaskTemplate> templates = taskTemplateService.getTemplatesByCategory(category);
        return Result.success(templates);
    }

    /**
     * 获取热门模板
     */
    @GetMapping("/popular")
    @SaCheckPermission("task:template:list")
    @Operation(summary = "获取热门模板")
    public Result<List<TTaskTemplate>> getPopularTemplates(@RequestParam(defaultValue = "10") int limit) {
        List<TTaskTemplate> templates = taskTemplateService.getPopularTemplates(limit);
        return Result.success(templates);
    }

    /**
     * 导入模板
     */
    @PostMapping("/import")
    @SaCheckPermission("task:template:create")
    @Operation(summary = "导入模板")
    public Result<Void> importTemplate(@RequestBody TTaskTemplate template) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        template.setCreatorId(currentUserId);
        
        boolean success = taskTemplateService.importTemplate(template);
        return success ? Result.success() : Result.failed("导入模板失败");
    }

    /**
     * 应用模板请求体
     */
    public static class ApplyTemplateRequest {
        private Long projectId;
        private Long assigneeId;
        private String startDate;
        private String taskPrefix;

        // Getters and Setters
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        
        public Long getAssigneeId() { return assigneeId; }
        public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
        
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        
        public String getTaskPrefix() { return taskPrefix; }
        public void setTaskPrefix(String taskPrefix) { this.taskPrefix = taskPrefix; }
    }

    /**
     * 克隆模板请求体
     */
    public static class CloneTemplateRequest {
        private String newName;

        public String getNewName() { return newName; }
        public void setNewName(String newName) { this.newName = newName; }
    }
}