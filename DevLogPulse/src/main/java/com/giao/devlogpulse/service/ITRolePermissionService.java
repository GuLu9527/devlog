package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TRolePermission;
import com.giao.devlogpulse.entity.po.TPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限关联表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
public interface ITRolePermissionService extends IService<TRolePermission> {

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 检查权限是否被角色使用
     *
     * @param permissionId 权限ID
     * @return 是否被使用
     */
    boolean isUsedByRole(Long permissionId);
    
    /**
     * 获取角色拥有的所有权限编码
     *
     * @param roleIds 角色ID列表
     * @return 权限编码列表
     */
    List<String> getPermissionCodesByRoleIds(List<Long> roleIds);
    
    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<TPermission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 为角色分配权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
