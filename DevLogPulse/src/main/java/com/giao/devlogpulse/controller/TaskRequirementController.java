package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.entity.po.TTaskRequirement;
import com.giao.devlogpulse.model.dto.TaskRequirementUpdateDTO;
import com.giao.devlogpulse.service.ITTaskRequirementService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务技能需求管理Controller
 */
@RestController
@RequestMapping("/task-requirements")
public class TaskRequirementController {

    @Autowired
    private ITTaskRequirementService taskRequirementService;

    /**
     * 获取任务技能需求列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<Map<String, Object>>> getTaskRequirements(@PathVariable Long taskId) {
        List<Map<String, Object>> requirements = taskRequirementService.getTaskRequirementsWithTags(taskId);
        return Result.success(requirements);
    }

    /**
     * 根据标签查询需要该技能的任务
     */
    @GetMapping("/tag/{tagId}/tasks")
    public Result<List<Map<String, Object>>> getTasksBySkillTag(@PathVariable Long tagId) {
        List<Map<String, Object>> tasks = taskRequirementService.getTasksBySkillTag(tagId);
        return Result.success(tasks);
    }

    /**
     * 更新任务技能需求
     */
    @PostMapping("/update")
    public Result<String> updateTaskRequirements(@Valid @RequestBody TaskRequirementUpdateDTO updateDTO) {
        try {
            List<TTaskRequirement> requirements = updateDTO.getRequirements().stream().map(dto -> {
                TTaskRequirement requirement = new TTaskRequirement();
                BeanUtils.copyProperties(dto, requirement);
                return requirement;
            }).collect(Collectors.toList());
            
            boolean success = taskRequirementService.updateTaskRequirements(updateDTO.getTaskId(), requirements);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.failed("更新失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 添加任务技能需求
     */
    @PostMapping
    public Result<String> addTaskRequirement(@Valid @RequestBody TaskRequirementUpdateDTO.TaskRequirementItemDTO requirementDTO,
                                           @RequestParam Long taskId) {
        try {
            TTaskRequirement requirement = new TTaskRequirement();
            BeanUtils.copyProperties(requirementDTO, requirement);
            requirement.setTaskId(taskId);
            
            boolean success = taskRequirementService.addTaskRequirement(requirement);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.failed("添加失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 删除任务技能需求
     */
    @DeleteMapping("/task/{taskId}/tag/{tagId}")
    public Result<String> removeTaskRequirement(@PathVariable Long taskId, @PathVariable Long tagId) {
        try {
            boolean success = taskRequirementService.removeTaskRequirement(taskId, tagId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.failed("删除失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 根据用户技能匹配适合的任务
     */
    @GetMapping("/suitable-tasks/user/{userId}")
    public Result<List<Map<String, Object>>> findSuitableTasksForUser(@PathVariable Long userId) {
        List<Map<String, Object>> suitableTasks = taskRequirementService.findSuitableTasksForUser(userId);
        return Result.success(suitableTasks);
    }

    /**
     * 获取技能需求统计
     */
    @GetMapping("/statistics/skill-demand")
    public Result<List<Map<String, Object>>> getSkillDemandStatistics() {
        List<Map<String, Object>> statistics = taskRequirementService.getSkillDemandStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取任务匹配度分析
     */
    @GetMapping("/analysis/task/{taskId}/user/{userId}")
    public Result<Map<String, Object>> getTaskMatchAnalysis(@PathVariable Long taskId, @PathVariable Long userId) {
        Map<String, Object> analysis = taskRequirementService.getTaskMatchAnalysis(taskId, userId);
        return Result.success(analysis);
    }

    /**
     * 推荐任务给用户
     */
    @GetMapping("/recommendations/user/{userId}")
    public Result<List<Map<String, Object>>> recommendTasksForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> recommendations = taskRequirementService.recommendTasksForUser(userId, limit);
        return Result.success(recommendations);
    }

    /**
     * 获取项目技能需求概览
     */
    @GetMapping("/statistics/project/{projectId}/skill-overview")
    public Result<Map<String, Object>> getProjectSkillOverview(@PathVariable Long projectId) {
        Map<String, Object> overview = taskRequirementService.getProjectSkillOverview(projectId);
        return Result.success(overview);
    }

    /**
     * 批量更新任务需求
     */
    @PutMapping("/batch-update")
    public Result<String> batchUpdateTaskRequirements(@Valid @RequestBody TaskRequirementUpdateDTO updateDTO) {
        try {
            List<TTaskRequirement> requirements = updateDTO.getRequirements().stream().map(dto -> {
                TTaskRequirement requirement = new TTaskRequirement();
                BeanUtils.copyProperties(dto, requirement);
                return requirement;
            }).collect(Collectors.toList());
            
            boolean success = taskRequirementService.batchUpdateTaskRequirements(updateDTO.getTaskId(), requirements);
            if (success) {
                return Result.success("批量更新成功");
            } else {
                return Result.failed("批量更新失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取技能需求热力图数据
     */
    @GetMapping("/statistics/skill-heatmap")
    public Result<List<Map<String, Object>>> getSkillDemandHeatmap(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) Long projectId) {
        // TODO: 实现技能需求热力图统计
        return Result.success(null);
    }

    /**
     * 获取任务分配建议
     */
    @GetMapping("/suggestions/task/{taskId}")
    public Result<List<Map<String, Object>>> getTaskAssignmentSuggestions(@PathVariable Long taskId) {
        // TODO: 实现基于技能匹配的任务分配建议
        return Result.success(null);
    }

    /**
     * 获取技能缺口分析
     */
    @GetMapping("/analysis/skill-gap")
    public Result<List<Map<String, Object>>> getSkillGapAnalysis(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        // TODO: 实现技能缺口分析
        return Result.success(null);
    }
}