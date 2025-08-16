package com.giao.devlogpulse.model.vo;

import com.giao.devlogpulse.entity.po.TWorkHour;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工时记录VO
 */
@Data
@Accessors(chain = true)
public class WorkHourVO {
    
    /**
     * 工时记录ID
     */
    private Long id;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务标题
     */
    private String taskTitle;
    
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 工作日期
     */
    private LocalDate workDate;
    
    /**
     * 工时数
     */
    private BigDecimal hours;
    
    /**
     * 工时类型
     */
    private TWorkHour.WorkHourType type;
    
    /**
     * 工时类型描述
     */
    private String typeText;
    
    /**
     * 工时描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态
     */
    private TWorkHour.Status status;
    
    /**
     * 状态描述
     */
    private String statusText;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核人姓名
     */
    private String reviewerName;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 审核备注
     */
    private String reviewNote;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}