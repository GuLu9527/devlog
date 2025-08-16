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
 * 任务附件表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task_attachment")
public class TTaskAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * OSS文件路径
     */
    private String ossPath;

    /**
     * 上传人ID
     */
    private Long uploaderId;

    /**
     * 是否删除(1是 0否)
     */
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 