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
 * 日志模板表
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_log_template")
public class TLogTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板内容（JSON格式）
     */
    private String content;

    /**
     * 适用部门ID
     */
    private Long departmentId;

    /**
     * 是否默认模板
     */
    private Integer isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
