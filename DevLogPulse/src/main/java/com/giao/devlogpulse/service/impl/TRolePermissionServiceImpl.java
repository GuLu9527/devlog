package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TRolePermission;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.mapper.TRolePermissionMapper;
import com.giao.devlogpulse.service.ITRolePermissionService;
import com.giao.devlogpulse.service.ITPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色权限关联表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TRolePermissionServiceImpl extends ServiceImpl<TRolePermissionMapper, TRolePermission> implements ITRolePermissionService {

    @Autowired
    private ITPermissionService permissionService;

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return this.lambdaQuery()
                .eq(TRolePermission::getRoleId, roleId)
                .list()
                .stream()
                .map(TRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUsedByRole(Long permissionId) {
        return this.lambdaQuery()
                .eq(TRolePermission::getPermissionId, permissionId)
                .exists();
    }
    
    @Override
    public List<String> getPermissionCodesByRoleIds(List<Long> roleIds) {
        // 1. 获取角色拥有的所有权限ID
        List<Long> permissionIds = this.lambdaQuery()
                .in(TRolePermission::getRoleId, roleIds)
                .list()
                .stream()
                .map(TRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
                
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        // 2. 获取这些权限的编码
        return baseMapper.selectPermissionCodesByIds(permissionIds);
    }
    
    @Override
    public List<TPermission> getPermissionsByRoleId(Long roleId) {
        // 1. 获取角色拥有的权限ID列表
        List<Long> permissionIds = getPermissionIdsByRoleId(roleId);
        
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        // 2. 根据权限ID列表查询权限信息
        return permissionService.listByIds(permissionIds);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 1. 删除角色原有的所有权限
        this.lambdaUpdate()
                .eq(TRolePermission::getRoleId, roleId)
                .remove();
        
        // 2. 如果有新权限，则添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            // 验证权限ID的有效性
            List<Long> validPermissionIds = permissionService.listByIds(permissionIds)
                    .stream()
                    .map(TPermission::getId)
                    .collect(Collectors.toList());
            
            if (!validPermissionIds.isEmpty()) {
                List<TRolePermission> rolePermissions = validPermissionIds.stream()
                        .map(permissionId -> {
                            TRolePermission rolePermission = new TRolePermission();
                            rolePermission.setRoleId(roleId);
                            rolePermission.setPermissionId(permissionId);
                            rolePermission.setCreateTime(LocalDateTime.now());
                            return rolePermission;
                        })
                        .collect(Collectors.toList());
                
                this.saveBatch(rolePermissions);
            }
        }
    }
}
