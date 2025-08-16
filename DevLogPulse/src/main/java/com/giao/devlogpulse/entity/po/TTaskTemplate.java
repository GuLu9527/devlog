package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务模板表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_task_template", autoResultMap = true)
public class TTaskTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板分类
     */
    private String category;

    /**
     * 模板标签，JSON数组格式
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 任务配置，JSON格式
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<TaskTemplateItem> tasks;

    /**
     * 任务数量
     */
    private Integer tasksCount;

    /**
     * 预计总工时
     */
    private BigDecimal estimatedHours;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsed;

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
     * 任务模板项
     */
    @Data
    public static class TaskTemplateItem implements Serializable {
        /**
         * 任务标题
         */
        private String title;

        /**
         * 任务描述
         */
        private String description;

        /**
         * 优先级
         */
        private String priority;

        /**
         * 预计工时
         */
        private BigDecimal estimatedHours;

        /**
         * 排序
         */
        private Integer order;
    }
}