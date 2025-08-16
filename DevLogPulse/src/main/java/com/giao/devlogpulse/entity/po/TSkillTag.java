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
 * 技术栈标签表
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_skill_tag")
public class TSkillTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签分类(前端/后端/数据库/工具等)
     */
    private String category;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 状态(1启用 0禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 更新者ID
     */
    private Long updatedBy;
}