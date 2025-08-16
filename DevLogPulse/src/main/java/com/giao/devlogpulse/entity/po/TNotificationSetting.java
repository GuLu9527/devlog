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
 * 通知设置表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_notification_setting")
public class TNotificationSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 任务分配通知(0关闭 1开启)
     */
    private Integer taskAssign;

    /**
     * 任务截止提醒(0关闭 1开启)
     */
    private Integer taskDeadline;

    /**
     * 日志审核通知(0关闭 1开启)
     */
    private Integer worklogReview;

    /**
     * 项目更新通知(0关闭 1开启)
     */
    private Integer projectUpdate;

    /**
     * 系统消息通知(0关闭 1开启)
     */
    private Integer systemMessage;

    /**
     * 邮件通知(0关闭 1开启)
     */
    private Integer emailNotify;

    /**
     * 截止提醒提前小时数
     */
    private Integer deadlineHours;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}