package com.giao.devlogpulse.common.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * 任务进度更新事件
 * 当任务进度发生变化时发布此事件，用于触发相关的业务逻辑（如项目进度更新）
 * 
 * @author giao
 * @since 2025-08-06
 */
@Data
public class TaskProgressUpdateEvent extends ApplicationEvent {
    
    /**
     * 任务ID
     */
    private final Long taskId;
    
    /**
     * 项目ID（如果任务关联了项目）
     */
    private final Long projectId;
    
    /**
     * 新增的工作时长
     */
    private final BigDecimal workLogHours;
    
    /**
     * 更新前的任务进度
     */
    private final Integer oldProgress;
    
    /**
     * 更新后的任务进度
     */
    private final Integer newProgress;
    
    /**
     * 事件类型
     */
    private final EventType eventType;
    
    public TaskProgressUpdateEvent(Object source, Long taskId, Long projectId, 
                                 BigDecimal workLogHours, Integer oldProgress, 
                                 Integer newProgress, EventType eventType) {
        super(source);
        this.taskId = taskId;
        this.projectId = projectId;
        this.workLogHours = workLogHours;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
        this.eventType = eventType;
    }
    
    /**
     * 事件类型枚举
     */
    public enum EventType {
        /**
         * 基于工作日志的进度更新
         */
        WORK_LOG_UPDATE,
        
        /**
         * 手动进度重新计算
         */
        MANUAL_RECALCULATE,
        
        /**
         * 任务状态变更触发的进度更新
         */
        STATUS_CHANGE
    }
}