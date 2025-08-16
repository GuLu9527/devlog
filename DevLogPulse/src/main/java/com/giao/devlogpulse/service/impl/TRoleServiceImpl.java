package com.giao.devlogpulse.service.impl;

import com.giao.devlogpulse.entity.po.TRole;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.mapper.TRoleMapper;
import com.giao.devlogpulse.service.ITRoleService;
import com.giao.devlogpulse.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TRoleServiceImpl extends ServiceImpl<TRoleMapper, TRole> implements ITRoleService {

    @Autowired
    private ITUserService userService;

    @Override
    public boolean hasUsers(Long roleId) {
        return userService.lambdaQuery()
                .eq(TUser::getRoleId, roleId)
                .exists();
    }
}
