package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.model.dto.*;
import com.giao.devlogpulse.model.vo.UserVO;
import com.giao.devlogpulse.service.ITUserService;
import com.giao.devlogpulse.utils.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private ITUserService userService;
    
    @Autowired
    private PasswordUtil passwordUtil;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @SaCheckPermission("user:list")
    public Result<IPage<UserVO>> list(UserPageDTO pageDTO) {
        Page<TUser> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<TUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (StringUtils.hasText(pageDTO.getUsername())) {
            queryWrapper.like(TUser::getUsername, pageDTO.getUsername());
        }
        if (StringUtils.hasText(pageDTO.getRealName())) {
            queryWrapper.like(TUser::getRealName, pageDTO.getRealName());
        }
        if (StringUtils.hasText(pageDTO.getEmail())) {
            queryWrapper.like(TUser::getEmail, pageDTO.getEmail());
        }
        if (StringUtils.hasText(pageDTO.getPhone())) {
            queryWrapper.like(TUser::getPhone, pageDTO.getPhone());
        }
        if (pageDTO.getStatus() != null) {
            queryWrapper.eq(TUser::getStatus, pageDTO.getStatus());
        }
        if (pageDTO.getRoleId() != null) {
            queryWrapper.eq(TUser::getRoleId, pageDTO.getRoleId());
        }
        if (pageDTO.getDepartmentId() != null) {
            queryWrapper.eq(TUser::getDepartmentId, pageDTO.getDepartmentId());
        }
        if (pageDTO.getGroupId() != null) {
            queryWrapper.eq(TUser::getGroupId, pageDTO.getGroupId());
        }
        
        queryWrapper.orderByDesc(TUser::getCreateTime);
        
        IPage<TUser> userPage = userService.page(page, queryWrapper);
        
        // 转换为VO
        IPage<UserVO> voPage = userPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("user:list")
    public Result<UserVO> getById(@PathVariable Long id) {
        TUser user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = convertToVO(user);
        return Result.success(userVO);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @SaCheckPermission("user:create")
    public Result<Void> create(@Valid @RequestBody UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        if (userService.lambdaQuery().eq(TUser::getUsername, createDTO.getUsername()).exists()) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.hasText(createDTO.getEmail()) && 
            userService.lambdaQuery().eq(TUser::getEmail, createDTO.getEmail()).exists()) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        TUser user = new TUser();
        BeanUtils.copyProperties(createDTO, user);
        
        // 加密密码
        user.setPassword(passwordUtil.encode(createDTO.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userService.save(user);
        return Result.success();
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("user:update")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        TUser existUser = userService.getById(id);
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 检查用户名是否已被其他用户使用
        if (!existUser.getUsername().equals(updateDTO.getUsername()) &&
            userService.lambdaQuery().eq(TUser::getUsername, updateDTO.getUsername()).exists()) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateDTO.getEmail()) && 
            !updateDTO.getEmail().equals(existUser.getEmail()) &&
            userService.lambdaQuery().eq(TUser::getEmail, updateDTO.getEmail()).exists()) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        BeanUtils.copyProperties(updateDTO, existUser);
        existUser.setUpdateTime(LocalDateTime.now());
        
        userService.updateById(existUser);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        TUser user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // TODO: 检查用户是否可以删除（例如是否有关联的任务、日志等）
        
        userService.removeById(id);
        return Result.success();
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户ID列表不能为空");
        }
        
        userService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        // TODO: 从JWT中获取当前用户ID
        // 暂时使用硬编码，后续需要从JWT中解析
        Long currentUserId = 1L;
        
        TUser user = userService.getById(currentUserId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!passwordUtil.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 验证新密码和确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_NOT_MATCH);
        }
        
        // 验证新密码不能与旧密码相同
        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_SAME);
        }
        
        // 更新密码
        user.setPassword(passwordUtil.encode(changePasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @PostMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        TUser user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 更新密码
        user.setPassword(passwordUtil.encode(resetPasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        
        userService.updateById(user);
        return Result.success();
    }
    
    /**
     * 转换为VO
     */
    private UserVO convertToVO(TUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        // 设置状态文本
        vo.setStatusText(user.getStatus() == 1 ? "启用" : "禁用");
        
        // TODO: 查询关联信息（角色名称、部门名称、组名称）
        
        return vo;
    }
}
