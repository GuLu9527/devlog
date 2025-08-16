package com.giao.devlogpulse.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task")
public class TTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    private Status status;

    /**
     * 任务优先级
     */
    private Priority priority;
    
    /**
     * 任务状态枚举
     */
    public enum Status {
        待处理, 进行中, 审核中, 已完成, 已取消
    }
    
    /**
     * 任务优先级枚举
     */
    public enum Priority {
        高, 中, 低
    }

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
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;

    /**
     * 删除状态(0=可删除,1=不可删除)
     */
    private Integer deleteStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
