package com.giao.devlogpulse.model.dto;

import com.giao.devlogpulse.entity.po.TTask;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务更新DTO
 */
@Data
public class TaskUpdateDTO {
    
    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过200个字符")
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
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
     * 负责人ID
     */
    private Long assigneeId;
    
    /**
     * 所属部门ID
     */
    @NotNull(message = "所属部门不能为空")
    private Long departmentId;
    
    /**
     * 所属组ID
     */
    private Long groupId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 预估工时(小时)
     */
    @DecimalMin(value = "0.0", message = "预估工时不能为负数")
    private BigDecimal estimatedHours;
    
    /**
     * 实际工时(小时)
     */
    @DecimalMin(value = "0.0", message = "实际工时不能为负数")
    private BigDecimal actualHours;
    
    /**
     * 进度(0-100)
     */
    @Min(value = 0, message = "进度不能小于0")
    @Max(value = 100, message = "进度不能大于100")
    private Integer progress;
    
    /**
     * 依赖任务ID
     */
    private Long dependTaskId;
}