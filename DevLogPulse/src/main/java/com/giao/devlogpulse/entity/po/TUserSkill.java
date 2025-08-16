package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户技能关联表
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_skill")
public class TUserSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 熟练程度(初级/中级/高级/专家)
     */
    private String proficiencyLevel;

    /**
     * 使用年限
     */
    private Integer yearsOfExperience;

    /**
     * 最后使用时间
     */
    private LocalDate lastUsed;

    /**
     * 是否主要技能(1是 0否)
     */
    private Integer isPrimary;

    /**
     * 自评分数(1-10分)
     */
    private Integer selfRating;

    /**
     * 备注
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