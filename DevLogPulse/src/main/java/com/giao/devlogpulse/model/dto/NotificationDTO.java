package com.giao.devlogpulse.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知数据传输对象
 */
@Data
public class NotificationDTO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;

    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;

    /**
     * 通知类型
     */
    @NotBlank(message = "通知类型不能为空")
    private String type;

    /**
     * 通知级别
     */
    private String level;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 接收人ID
     */
    @NotNull(message = "接收人ID不能为空")
    private Long receiverId;

    /**
     * 关联业务ID
     */
    private Long relatedId;

    /**
     * 关联业务类型
     */
    private String relatedType;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发送人姓名
     */
    private String senderName;

    /**
     * 模板变量(用于模板通知)
     */
    private Map<String, Object> variables;
} 