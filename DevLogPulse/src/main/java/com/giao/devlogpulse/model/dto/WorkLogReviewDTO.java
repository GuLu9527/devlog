package com.giao.devlogpulse.model.dto;

import com.giao.devlogpulse.entity.po.TWorkLog;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 工作日志审核DTO
 */
@Data
public class WorkLogReviewDTO {
    
    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    private TWorkLog.ReviewStatus reviewStatus;
    
    /**
     * 审核意见
     */
    @Size(max = 500, message = "审核意见长度不能超过500个字符")
    private String reviewComment;
}