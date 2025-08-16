package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工作日志创建DTO
 */
@Data
public class WorkLogCreateDTO {
    
    /**
     * 关联任务ID
     */
    @NotNull(message = "关联任务不能为空")
    private Long taskId;
    
    /**
     * 日志内容
     */
    @NotBlank(message = "日志内容不能为空")
    @Size(max = 2000, message = "日志内容长度不能超过2000个字符")
    private String content;
    
    /**
     * 工时(小时)
     */
    @NotNull(message = "工时不能为空")
    @DecimalMin(value = "0.1", message = "工时不能小于0.1小时")
    @DecimalMax(value = "24.0", message = "工时不能超过24小时")
    private BigDecimal hours;
    
    /**
     * 日志日期
     */
    @NotNull(message = "日志日期不能为空")
    private LocalDate logDate;
    
    /**
     * 所属组ID
     */
    @NotNull(message = "所属组不能为空")
    private Long groupId;
}