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
 * 权限表
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_permission")
public class TPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 接口URL
     */
    private String url;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 权限描述
     */
    private String description;

    private LocalDateTime createTime;


}
