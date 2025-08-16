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
 * 用户表
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 所属部门ID
     */
    private Long departmentId;

    /**
     * 所属组ID
     */
    private Long groupId;

    /**
     * 状态(1启用 0禁用)
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
