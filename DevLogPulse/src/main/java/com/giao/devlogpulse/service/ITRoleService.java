package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
public interface ITRoleService extends IService<TRole> {

    /**
     * 检查角色下是否有用户
     *
     * @param roleId 角色ID
     * @return 是否有用户
     */
    boolean hasUsers(Long roleId);
}
