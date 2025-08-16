package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TPermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
public interface ITPermissionService extends IService<TPermission> {

    /**
     * 检查权限是否被角色使用
     *
     * @param permissionId 权限ID
     * @return 是否被使用
     */
    boolean isUsedByRole(Long permissionId);
    
    /**
     * 检查用户是否有指定权限
     *
     * @param userId 用户ID
     * @param permissionCode 权限编码
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, String permissionCode);
}
