package com.giao.devlogpulse.model.dto;

import com.giao.devlogpulse.entity.po.TTask;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务分页查询DTO
 */
@Data
public class TaskPageDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务状态
     */
    private TTask.Status status;
    
    /**
     * 优先级
     */
    private TTask.Priority priority;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 创建人ID
     */
    private Long creatorId;
    
    /**
     * 负责人ID
     */
    private Long assigneeId;
    
    /**
     * 所属部门ID
     */
    private Long departmentId;
    
    /**
     * 所属组ID
     */
    private Long groupId;
    
    /**
     * 开始时间范围
     */
    private LocalDateTime startTimeBegin;
    private LocalDateTime startTimeEnd;
    
    /**
     * 截止时间范围
     */
    private LocalDateTime deadlineBegin;
    private LocalDateTime deadlineEnd;
}