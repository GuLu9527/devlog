package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TPermission;
import com.giao.devlogpulse.model.dto.UserLoginDTO;
import com.giao.devlogpulse.model.dto.UserLoginVO;
import com.giao.devlogpulse.model.dto.UserRegisterDTO;
import com.giao.devlogpulse.service.ITUserService;
import com.giao.devlogpulse.service.ITRoleService;
import com.giao.devlogpulse.service.ITRolePermissionService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.giao.devlogpulse.utils.PasswordUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private ITUserService userService;

    @Autowired
    private ITRoleService roleService;
    
    @Autowired
    private ITRolePermissionService rolePermissionService;

    
    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.lambdaQuery().eq(TUser::getUsername, registerDTO.getUsername()).exists()) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (registerDTO.getEmail() != null && 
            userService.lambdaQuery().eq(TUser::getEmail, registerDTO.getEmail()).exists()) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        // 检查确认密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_NOT_MATCH);
        }

        // 创建用户对象
        TUser user = new TUser();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 加密密码（使用BCrypt）
        user.setPassword(passwordUtil.encode(registerDTO.getPassword()));
        user.setStatus(1); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        user.setRoleId(3L);
        userService.save(user);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Validated @RequestBody UserLoginDTO loginDTO) {
        // 查询用户
        TUser user = userService.lambdaQuery()
                .eq(TUser::getUsername, loginDTO.getUsername())
                .eq(TUser::getStatus, 1)
                .one();

        // 验证用户存在
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        // 验证密码（使用BCrypt）
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 使用SA-Token进行登录，传入用户ID
        StpUtil.login(user.getId());
        
        // 获取生成的token
        String token = StpUtil.getTokenValue();

        // 更新用户最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);

        // 构建登录响应VO
        UserLoginVO loginVO = new UserLoginVO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setRealName(user.getRealName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone())
                .setAvatar(user.getAvatar())
                .setRoleId(user.getRoleId())
                .setDepartmentId(user.getDepartmentId())
                .setGroupId(user.getGroupId())
                .setToken(token)
                .setTokenExpireTime(LocalDateTime.now().plusSeconds(StpUtil.getTokenTimeout()));
        
        // 查询角色信息和权限列表
        if (user.getRoleId() != null) {
            TRole role = roleService.getById(user.getRoleId());
            if (role != null) {
                loginVO.setRoleName(role.getName());
                
                // 查询用户权限列表
                List<TPermission> permissions = rolePermissionService.getPermissionsByRoleId(user.getRoleId());
                List<String> permissionCodes = permissions.stream()
                        .map(TPermission::getCode)
                        .collect(java.util.stream.Collectors.toList());
                loginVO.setPermissions(permissionCodes);
                
                log.info("用户 {} 登录成功，角色：{}，权限数量：{}", user.getUsername(), role.getName(), permissionCodes.size());
                log.debug("用户权限列表：{}", permissionCodes);
            }
        }
        
        return Result.success(loginVO);
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 使用SA-Token进行登出，会自动删除token
        StpUtil.logout();
        return Result.success();
    }
}