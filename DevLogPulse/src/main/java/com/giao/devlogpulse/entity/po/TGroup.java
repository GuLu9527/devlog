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
 * 小组表
 * </p>
 *
 * @author giao
 * @since 2025-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_group")
public class TGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 小组ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小组名称
     */
    private String name;

    /**
     * 小组描述
     */
    private String description;

    /**
     * 所属部门ID
     */
    private Long departmentId;

    /**
     * 组长用户ID
     */
    private Long leaderId;

    /**
     * 状态(1启用 0禁用)
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
