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
 * 角色表
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_role")
public class TRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称(管理员/组长/组员)
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
