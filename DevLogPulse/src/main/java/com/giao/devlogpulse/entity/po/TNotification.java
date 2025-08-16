package com.giao.devlogpulse.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_notification")
public class TNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知级别
     */
    private String level;

    /**
     * 发送人ID(系统消息为NULL)
     */
    private Long senderId;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 关联业务ID(任务ID、日志ID等)
     */
    private Long relatedId;

    /**
     * 关联业务类型(task、worklog、project等)
     */
    private String relatedType;

    /**
     * 是否已读(0未读 1已读)
     */
    private Integer isRead;

    /**
     * 是否删除(0否 1是)
     */
    private Integer isDeleted;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // 通知类型常量
    public static class NotificationType {
        public static final String TASK_ASSIGN = "TASK_ASSIGN";
        public static final String WORKLOG_REVIEW = "WORKLOG_REVIEW";
        public static final String TASK_DEADLINE = "TASK_DEADLINE";
        public static final String SYSTEM_MESSAGE = "SYSTEM_MESSAGE";
        public static final String PROJECT_UPDATE = "PROJECT_UPDATE";
    }

    // 通知级别常量
    public static class NotificationLevel {
        public static final String NORMAL = "普通";
        public static final String IMPORTANT = "重要";
        public static final String URGENT = "紧急";
    }
} 