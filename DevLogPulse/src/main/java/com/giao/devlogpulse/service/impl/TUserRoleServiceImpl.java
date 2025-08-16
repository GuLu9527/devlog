package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TUserRole;
import com.giao.devlogpulse.mapper.TUserRoleMapper;
import com.giao.devlogpulse.service.ITUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TUserRoleServiceImpl extends ServiceImpl<TUserRoleMapper, TUserRole> implements ITUserRoleService {

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(TUserRole::getUserId, userId)
                .list()
                .stream()
                .map(TUserRole::getRoleId)
                .collect(Collectors.toList());
    }
} 