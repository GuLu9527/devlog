package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
public interface ITUserRoleService extends IService<TUserRole> {

    /**
     * 获取用户的所有角色ID
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
} 