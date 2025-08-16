package com.giao.devlogpulse.model.vo;

import com.giao.devlogpulse.entity.po.TTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务VO
 */
@Data
@Accessors(chain = true)
public class TaskVO {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 任务标题
     */
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
     * 状态描述
     */
    private String statusText;
    
    /**
     * 优先级
     */
    private TTask.Priority priority;
    
    /**
     * 优先级描述
     */
    private String priorityText;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 创建人ID
     */
    private Long creatorId;
    
    /**
     * 创建人姓名
     */
    private String creatorName;
    
    /**
     * 负责人ID
     */
    private Long assigneeId;
    
    /**
     * 负责人姓名
     */
    private String assigneeName;
    
    /**
     * 所属部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 所属组ID
     */
    private Long groupId;
    
    /**
     * 组名称
     */
    private String groupName;
    
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
    private BigDecimal estimatedHours;
    
    /**
     * 实际工时(小时)
     */
    private BigDecimal actualHours;
    
    /**
     * 进度(0-100)
     */
    private Integer progress;
    
    /**
     * 依赖任务ID
     */
    private Long dependTaskId;
    
    /**
     * 依赖任务标题
     */
    private String dependTaskTitle;
    
    /**
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;
    
    /**
     * 删除状态(0=可删除,1=不可删除)
     */
    private Integer deleteStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否逾期
     */
    private Boolean overdue;
    
    /**
     * 剩余天数
     */
    private Long remainingDays;
}