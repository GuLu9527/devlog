package com.giao.devlogpulse.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工作日志表
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_work_log")
public class TWorkLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 提交人ID
     */
    private Long userId;

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
    private ReviewStatus reviewStatus;
    
    /**
     * 审核状态枚举
     */
    public enum ReviewStatus {
        待审核, 已通过, 已拒绝
    }

    /**
     * 审核人ID
     */
    private Long reviewerId;

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
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
