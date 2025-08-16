package com.giao.devlogpulse.service.impl;

import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.entity.po.TRolePermission;
import com.giao.devlogpulse.mapper.TPermissionMapper;
import com.giao.devlogpulse.mapper.TRolePermissionMapper;
import com.giao.devlogpulse.service.ITPermissionService;
import com.giao.devlogpulse.service.ITUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TPermissionServiceImpl extends ServiceImpl<TPermissionMapper, TPermission> implements ITPermissionService {

    @Autowired
    private ITUserRoleService userRoleService;
    
    @Autowired
    private TRolePermissionMapper rolePermissionMapper;

    @Override
    public boolean isUsedByRole(Long permissionId) {
        return rolePermissionMapper.exists(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TRolePermission>()
                .eq(TRolePermission::getPermissionId, permissionId)
        );
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        // 1. 获取用户的所有角色ID
        List<Long> roleIds = userRoleService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        
        // 2. 获取这些角色拥有的所有权限编码
        List<String> permissionCodes = rolePermissionMapper.selectPermissionCodesByRoleIds(roleIds);
        
        // 3. 检查是否包含指定权限
        return permissionCodes.contains(permissionCode);
    }
}
