package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.giao.devlogpulse.entity.po.TProject;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.mapper.TProjectMapper;
import com.giao.devlogpulse.service.ITProjectService;
import com.giao.devlogpulse.service.ITTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TProjectServiceImpl extends ServiceImpl<TProjectMapper, TProject> implements ITProjectService {

    private final ITTaskService taskService;

    @Override
    public Map<String, Object> calculateProjectProgress(Long projectId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询项目下所有任务
            LambdaQueryWrapper<TTask> taskQuery = new LambdaQueryWrapper<>();
            taskQuery.eq(TTask::getProjectId, projectId)
                    .eq(TTask::getIsDeleted, 0);
            
            List<TTask> tasks = taskService.list(taskQuery);
            
            if (tasks.isEmpty()) {
                result.put("progressPercentage", 0);
                result.put("totalTasks", 0);
                result.put("completedTasks", 0);
                result.put("inProgressTasks", 0);
                result.put("pendingTasks", 0);
                result.put("totalEstimatedHours", BigDecimal.ZERO);
                result.put("totalActualHours", BigDecimal.ZERO);
                return result;
            }
            
            // 统计任务状态
            long totalTasks = tasks.size();
            long completedTasks = tasks.stream().mapToLong(task -> 
                task.getStatus() == TTask.Status.已完成 ? 1 : 0).sum();
            long inProgressTasks = tasks.stream().mapToLong(task -> 
                task.getStatus() == TTask.Status.进行中 ? 1 : 0).sum();
            long pendingTasks = tasks.stream().mapToLong(task -> 
                task.getStatus() == TTask.Status.待处理 ? 1 : 0).sum();
            
            // 统计任务优先级
            long highPriorityTasks = tasks.stream().mapToLong(task -> 
                task.getPriority() == TTask.Priority.高 ? 1 : 0).sum();
            long mediumPriorityTasks = tasks.stream().mapToLong(task -> 
                task.getPriority() == TTask.Priority.中 ? 1 : 0).sum();
            long lowPriorityTasks = tasks.stream().mapToLong(task -> 
                task.getPriority() == TTask.Priority.低 ? 1 : 0).sum();
            
            // 统计工时
            BigDecimal totalEstimatedHours = tasks.stream()
                .map(task -> task.getEstimatedHours() != null ? task.getEstimatedHours() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalActualHours = tasks.stream()
                .map(task -> task.getActualHours() != null ? task.getActualHours() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算综合进度（基于任务完成度和工时完成度的平均值）
            int progressByTaskCount = (int) ((completedTasks * 100) / totalTasks);
            
            int progressByWorkHours = 0;
            if (totalEstimatedHours.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal hoursProgress = totalActualHours.multiply(BigDecimal.valueOf(100))
                    .divide(totalEstimatedHours, 2, RoundingMode.HALF_UP);
                progressByWorkHours = Math.min(hoursProgress.intValue(), 100);
            }
            
            // 综合进度：任务完成度占60%，工时完成度占40%
            int overallProgress = (int) (progressByTaskCount * 0.6 + progressByWorkHours * 0.4);
            
            result.put("progressPercentage", overallProgress);
            result.put("totalTasks", totalTasks);
            result.put("completedTasks", completedTasks);
            result.put("inProgressTasks", inProgressTasks);
            result.put("pendingTasks", pendingTasks);
            result.put("highPriorityTasks", highPriorityTasks);
            result.put("mediumPriorityTasks", mediumPriorityTasks);
            result.put("lowPriorityTasks", lowPriorityTasks);
            result.put("totalEstimatedHours", totalEstimatedHours);
            result.put("totalActualHours", totalActualHours);
            result.put("progressByTaskCount", progressByTaskCount);
            result.put("progressByWorkHours", progressByWorkHours);
            
            log.debug("项目进度计算完成 - 项目ID: {}, 综合进度: {}%, 任务进度: {}%, 工时进度: {}%", 
                projectId, overallProgress, progressByTaskCount, progressByWorkHours);
            
        } catch (Exception e) {
            log.error("计算项目进度失败，项目ID: {}", projectId, e);
            result.put("error", "计算失败");
        }
        
        return result;
    }

    @Override
    public boolean updateProjectProgress(Long projectId) {
        try {
            TProject project = getById(projectId);
            if (project == null || project.getIsDeleted() == 1) {
                log.warn("项目不存在或已删除，无法更新进度: {}", projectId);
                return false;
            }
            
            Map<String, Object> progressData = calculateProjectProgress(projectId);
            Integer progressPercentage = (Integer) progressData.get("progressPercentage");
            
            // 根据进度自动调整项目状态
            String newStatus = determineProjectStatus(project.getStatus(), progressPercentage);
            
            if (!newStatus.equals(project.getStatus())) {
                TProject updateProject = new TProject();
                updateProject.setId(projectId);
                updateProject.setStatus(newStatus);
                
                boolean result = super.updateById(updateProject);
                if (result) {
                    log.info("项目状态自动更新 - 项目ID: {}, 新状态: {}, 进度: {}%", 
                        projectId, newStatus, progressPercentage);
                }
                return result;
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("更新项目进度失败，项目ID: {}", projectId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getProjectStatistics(Long projectId) {
        return calculateProjectProgress(projectId);
    }
    
    /**
     * 根据进度百分比确定项目状态
     */
    private String determineProjectStatus(String currentStatus, Integer progressPercentage) {
        // 如果项目已关闭或已上线，不自动更改状态
        if ("已关闭".equals(currentStatus) || "已上线".equals(currentStatus)) {
            return currentStatus;
        }
        
        if (progressPercentage >= 100) {
            return "测试中"; // 所有任务完成，进入测试阶段
        } else if (progressPercentage >= 80) {
            return "开发中"; // 接近完成，继续开发
        } else if (progressPercentage > 0) {
            return "开发中"; // 有进展，正在开发
        } else {
            return "规划中"; // 没有进展，仍在规划
        }
    }
}
