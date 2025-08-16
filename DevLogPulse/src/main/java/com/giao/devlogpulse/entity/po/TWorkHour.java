package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工时记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_work_hour")
public class TWorkHour implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 工作日期
     */
    private LocalDate workDate;

    /**
     * 工时数
     */
    private BigDecimal hours;

    /**
     * 工时类型：DEVELOPMENT-开发，TESTING-测试，MEETING-会议，RESEARCH-调研，OTHER-其他
     */
    private WorkHourType type;

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
     * 状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回
     */
    private Status status;

    /**
     * 审核人ID
     */
    private Long reviewerId;

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

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;

    /**
     * 工时类型枚举
     */
    public enum WorkHourType {
        DEVELOPMENT,  // 开发
        TESTING,      // 测试
        MEETING,      // 会议
        RESEARCH,     // 调研
        OTHER         // 其他
    }

    /**
     * 状态枚举
     */
    public enum Status {
        PENDING,   // 待审核
        APPROVED,  // 已通过
        REJECTED   // 已驳回
    }
}