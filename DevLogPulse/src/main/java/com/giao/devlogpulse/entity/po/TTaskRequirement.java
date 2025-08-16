package com.giao.devlogpulse.entity.po;

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
 * 任务技能需求关联表
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task_requirement")
public class TTaskRequirement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 所需标签ID
     */
    private Long requiredTagId;

    /**
     * 最低熟练程度要求
     */
    private String minimumProficiency;

    /**
     * 重要程度(1-5级，5最重要)
     */
    private Integer importance;

    /**
     * 是否必需(1必需 0可选)
     */
    private Integer isRequired;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}