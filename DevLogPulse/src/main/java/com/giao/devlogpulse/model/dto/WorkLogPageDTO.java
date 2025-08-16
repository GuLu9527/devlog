package com.giao.devlogpulse.model.dto;

import com.giao.devlogpulse.entity.po.TWorkLog;
import lombok.Data;

import java.time.LocalDate;

/**
 * 工作日志分页查询DTO
 */
@Data
public class WorkLogPageDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 关联任务ID
     */
    private Long taskId;
    
    /**
     * 提交人ID
     */
    private Long userId;
    
    /**
     * 所属组ID
     */
    private Long groupId;
    
    /**
     * 审核状态
     */
    private TWorkLog.ReviewStatus reviewStatus;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 日志内容关键词
     */
    private String content;
}