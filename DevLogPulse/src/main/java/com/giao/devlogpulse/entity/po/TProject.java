package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 项目表
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_project")
public class TProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    private String status;

    /**
     * 项目经理ID
     */
    private Long managerId;

    /**
     * 负责部门ID
     */
    private Long departmentId;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;

    /**
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
