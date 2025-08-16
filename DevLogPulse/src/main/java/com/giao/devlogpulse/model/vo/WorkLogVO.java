package com.giao.devlogpulse.model.vo;

import com.giao.devlogpulse.entity.po.TWorkLog;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作日志VO
 */
@Data
@Accessors(chain = true)
public class WorkLogVO {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 关联任务ID
     */
    private Long taskId;
    
    /**
     * 任务标题
     */
    private String taskTitle;
    
    /**
     * 提交人ID
     */
    private Long userId;
    
    /**
     * 提交人姓名
     */
    private String userName;
    
    /**
     * 日志内容
     */
    private String content;
    
    /**
     * 工时(小时)
     */
    private BigDecimal hours;
    
    /**
     * 日志日期
     */
    private LocalDate logDate;
    
    /**
     * 审核状态
     */
    private TWorkLog.ReviewStatus reviewStatus;
    
    /**
     * 审核状态描述
     */
    private String reviewStatusText;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核人姓名
     */
    private String reviewerName;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 所属组ID
     */
    private Long groupId;
    
    /**
     * 组名称
     */
    private String groupName;
    
    /**
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否可编辑
     */
    private Boolean editable;
    
    /**
     * 是否可删除
     */
    private Boolean deletable;
}