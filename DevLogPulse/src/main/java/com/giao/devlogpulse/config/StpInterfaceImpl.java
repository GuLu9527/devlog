package com.giao.devlogpulse.config;

import cn.dev33.satoken.stp.StpInterface;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.service.ITPermissionService;
import com.giao.devlogpulse.service.ITRolePermissionService;
import com.giao.devlogpulse.service.ITRoleService;
import com.giao.devlogpulse.service.ITUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SA-Token权限验证接口实现
 * 
 * @author giao
 */
@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private ITUserService userService;
    
    @Autowired
    private ITRoleService roleService;
    
    @Autowired
    private ITRolePermissionService rolePermissionService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        log.debug("SA-Token获取用户权限列表 - loginId: {}, loginType: {}", loginId, loginType);
        
        try {
            // 将loginId转换为Long类型的用户ID
            Long userId = Long.valueOf(loginId.toString());
            
            // 查询用户
            TUser user = userService.getById(userId);
            if (user == null || user.getRoleId() == null) {
                log.warn("用户不存在或未分配角色: {}", userId);
                return List.of();
            }
            
            // 查询用户权限
            List<TPermission> permissions = rolePermissionService.getPermissionsByRoleId(user.getRoleId());
            List<String> permissionCodes = permissions.stream()
                    .map(TPermission::getCode)
                    .collect(Collectors.toList());
            
            log.debug("用户 {} 拥有权限: {}", userId, permissionCodes);
            return permissionCodes;
            
        } catch (Exception e) {
            log.error("获取用户权限列表时发生异常 - loginId: {}", loginId, e);
            return List.of();
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.debug("SA-Token获取用户角色列表 - loginId: {}, loginType: {}", loginId, loginType);
        
        try {
            // 将loginId转换为Long类型的用户ID
            Long userId = Long.valueOf(loginId.toString());
            
            // 查询用户
            TUser user = userService.getById(userId);
            if (user == null || user.getRoleId() == null) {
                log.warn("用户不存在或未分配角色: {}", userId);
                return List.of();
            }
            
            // 查询用户角色
            TRole role = roleService.getById(user.getRoleId());
            if (role == null) {
                log.warn("角色不存在: {}", user.getRoleId());
                return List.of();
            }
            
            List<String> roleList = List.of(role.getName());
            log.debug("用户 {} 拥有角色: {}", userId, roleList);
            return roleList;
            
        } catch (Exception e) {
            log.error("获取用户角色列表时发生异常 - loginId: {}", loginId, e);
            return List.of();
        }
    }
}