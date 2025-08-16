package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务依赖关系表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task_dependency")
public class TTaskDependency implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID（被依赖的任务）
     */
    private Long taskId;

    /**
     * 依赖任务ID（依赖的任务）
     */
    private Long dependentTaskId;

    /**
     * 依赖类型：BLOCKS-阻塞关系，DEPENDS_ON-依赖关系
     */
    private DependencyType type;

    /**
     * 依赖描述
     */
    private String description;

    /**
     * 创建人ID
     */
    private Long creatorId;

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
     * 依赖类型枚举
     */
    public enum DependencyType {
        BLOCKS,      // 阻塞关系
        DEPENDS_ON   // 依赖关系
    }
}