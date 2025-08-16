package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TWorkLog;
import com.giao.devlogpulse.mapper.TTaskMapper;
import com.giao.devlogpulse.service.ITTaskService;
import com.giao.devlogpulse.service.ITWorkLogService;
import com.giao.devlogpulse.service.INotificationService;
import com.giao.devlogpulse.common.event.TaskProgressUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TTaskServiceImpl extends ServiceImpl<TTaskMapper, TTask> implements ITTaskService {

    private final INotificationService notificationService;
    private final ITWorkLogService workLogService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean save(TTask entity) {
        boolean result = super.save(entity);
        
        // 如果保存成功且指定了负责人，发送任务分配通知
        if (result && entity.getAssigneeId() != null && entity.getCreatorId() != null) {
            try {
                notificationService.sendTaskAssignNotification(entity.getId(), entity.getAssigneeId(), entity.getCreatorId());
            } catch (Exception e) {
                log.error("发送任务分配通知失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean updateById(TTask entity) {
        // 获取原任务信息
        TTask oldTask = getById(entity.getId());
        boolean result = super.updateById(entity);
        
        // 如果更新成功，检查是否需要发送通知
        if (result && oldTask != null) {
            try {
                // 检查负责人是否变更
                if (entity.getAssigneeId() != null && 
                    !entity.getAssigneeId().equals(oldTask.getAssigneeId())) {
                    // 发送任务重新分配通知
                    // 使用创建人ID作为分配者ID，如果没有则使用系统ID
                    Long assignerId = entity.getCreatorId() != null ? entity.getCreatorId() : 1L;
                    notificationService.sendTaskAssignNotification(entity.getId(), entity.getAssigneeId(), assignerId);
                }
            } catch (Exception e) {
                log.error("发送任务更新通知失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean updateTaskProgressByWorkLog(Long taskId, BigDecimal workLogHours) {
        try {
            TTask task = getById(taskId);
            if (task == null || task.getIsDeleted() == 1) {
                log.warn("任务不存在或已删除，无法更新进度: {}", taskId);
                return false;
            }

            // 记录原始进度
            Integer oldProgress = task.getProgress() != null ? task.getProgress() : 0;

            // 更新实际工时
            BigDecimal currentHours = task.getActualHours() != null ? task.getActualHours() : BigDecimal.ZERO;
            BigDecimal newActualHours = currentHours.add(workLogHours);

            // 计算新的进度百分比
            Integer newProgress = calculateProgressPercentage(task.getEstimatedHours(), newActualHours);
            
            // 根据进度自动更新任务状态
            TTask.Status newStatus = determineTaskStatus(task.getStatus(), newProgress);

            // 更新任务
            TTask updateTask = new TTask();
            updateTask.setId(taskId);
            updateTask.setActualHours(newActualHours);
            updateTask.setProgress(newProgress);
            updateTask.setStatus(newStatus);
            updateTask.setUpdateTime(LocalDateTime.now());

            boolean result = super.updateById(updateTask);
            
            if (result) {
                log.info("任务进度自动更新成功 - 任务ID: {}, 新增工时: {}h, 总工时: {}h, 进度: {}% -> {}%, 状态: {}", 
                    taskId, workLogHours, newActualHours, oldProgress, newProgress, newStatus);
                
                // 发布任务进度更新事件，由监听器处理项目进度更新
                if (task.getProjectId() != null) {
                    try {
                        TaskProgressUpdateEvent event = new TaskProgressUpdateEvent(
                            this, taskId, task.getProjectId(), workLogHours, 
                            oldProgress, newProgress, TaskProgressUpdateEvent.EventType.WORK_LOG_UPDATE
                        );
                        eventPublisher.publishEvent(event);
                        log.debug("任务进度更新事件已发布 - 任务ID: {}, 项目ID: {}", taskId, task.getProjectId());
                    } catch (Exception e) {
                        log.warn("发布任务进度更新事件失败，任务ID: {}, 项目ID: {}", taskId, task.getProjectId(), e);
                    }
                }
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("更新任务进度失败，任务ID: {}", taskId, e);
            return false;
        }
    }

    @Override
    public boolean recalculateTaskProgress(Long taskId) {
        try {
            TTask task = getById(taskId);
            if (task == null || task.getIsDeleted() == 1) {
                log.warn("任务不存在或已删除，无法重新计算进度: {}", taskId);
                return false;
            }

            // 记录原始进度
            Integer oldProgress = task.getProgress() != null ? task.getProgress() : 0;

            // 查询所有已通过审核的工作日志
            LambdaQueryWrapper<TWorkLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TWorkLog::getTaskId, taskId)
                       .eq(TWorkLog::getReviewStatus, TWorkLog.ReviewStatus.已通过)
                       .eq(TWorkLog::getIsDeleted, 0);
            
            List<TWorkLog> approvedLogs = workLogService.list(queryWrapper);
            
            // 计算总工时
            BigDecimal totalActualHours = approvedLogs.stream()
                .map(TWorkLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 计算进度百分比
            Integer newProgress = calculateProgressPercentage(task.getEstimatedHours(), totalActualHours);
            
            // 根据进度自动更新任务状态
            TTask.Status newStatus = determineTaskStatus(task.getStatus(), newProgress);

            // 更新任务
            TTask updateTask = new TTask();
            updateTask.setId(taskId);
            updateTask.setActualHours(totalActualHours);
            updateTask.setProgress(newProgress);
            updateTask.setStatus(newStatus);
            updateTask.setUpdateTime(LocalDateTime.now());

            boolean result = super.updateById(updateTask);
            
            if (result) {
                log.info("任务进度重新计算成功 - 任务ID: {}, 总工时: {}h, 进度: {}% -> {}%, 状态: {}", 
                    taskId, totalActualHours, oldProgress, newProgress, newStatus);
                
                // 发布任务进度更新事件，由监听器处理项目进度更新
                if (task.getProjectId() != null) {
                    try {
                        TaskProgressUpdateEvent event = new TaskProgressUpdateEvent(
                            this, taskId, task.getProjectId(), BigDecimal.ZERO, 
                            oldProgress, newProgress, TaskProgressUpdateEvent.EventType.MANUAL_RECALCULATE
                        );
                        eventPublisher.publishEvent(event);
                        log.debug("任务进度重新计算事件已发布 - 任务ID: {}, 项目ID: {}", taskId, task.getProjectId());
                    } catch (Exception e) {
                        log.warn("发布任务进度重新计算事件失败，任务ID: {}, 项目ID: {}", taskId, task.getProjectId(), e);
                    }
                }
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("重新计算任务进度失败，任务ID: {}", taskId, e);
            return false;
        }
    }

    /**
     * 计算进度百分比
     * 
     * @param estimatedHours 预估工时
     * @param actualHours 实际工时
     * @return 进度百分比 (0-100)
     */
    private Integer calculateProgressPercentage(BigDecimal estimatedHours, BigDecimal actualHours) {
        if (estimatedHours == null || estimatedHours.compareTo(BigDecimal.ZERO) <= 0) {
            // 如果没有预估工时，则根据实际工时判断
            return actualHours.compareTo(BigDecimal.ZERO) > 0 ? 50 : 0;
        }
        
        // 计算完成百分比，但不超过100%
        BigDecimal percentage = actualHours.multiply(BigDecimal.valueOf(100))
                                          .divide(estimatedHours, 2, RoundingMode.HALF_UP);
        
        int result = percentage.intValue();
        return Math.min(result, 100); // 最大不超过100%
    }

    /**
     * 根据进度百分比确定任务状态
     * 
     * @param currentStatus 当前状态
     * @param progressPercentage 进度百分比
     * @return 新的任务状态
     */
    private TTask.Status determineTaskStatus(TTask.Status currentStatus, Integer progressPercentage) {
        // 如果任务已取消或已完成，不自动更改状态
        if (currentStatus == TTask.Status.已取消 || currentStatus == TTask.Status.已完成) {
            return currentStatus;
        }
        
        if (progressPercentage >= 100) {
            return TTask.Status.已完成;
        } else if (progressPercentage > 0) {
            return TTask.Status.进行中;
        } else {
            return TTask.Status.待处理;
        }
    }
}
