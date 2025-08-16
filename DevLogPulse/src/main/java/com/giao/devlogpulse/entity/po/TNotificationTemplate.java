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
 * 通知模板表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_notification_template")
public class TNotificationTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 标题模板
     */
    private String titleTemplate;

    /**
     * 内容模板
     */
    private String contentTemplate;

    /**
     * 模板变量说明(JSON格式)
     */
    private String variables;

    /**
     * 是否启用
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}