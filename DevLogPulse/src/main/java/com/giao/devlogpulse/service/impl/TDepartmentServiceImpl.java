package com.giao.devlogpulse.service.impl;

import com.giao.devlogpulse.entity.po.TDepartment;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.mapper.TDepartmentMapper;
import com.giao.devlogpulse.service.ITDepartmentService;
import com.giao.devlogpulse.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TDepartmentServiceImpl extends ServiceImpl<TDepartmentMapper, TDepartment> implements ITDepartmentService {

    @Autowired
    private ITUserService userService;

    @Override
    public boolean hasUsers(Long departmentId) {
        return userService.lambdaQuery()
                .eq(TUser::getDepartmentId, departmentId)
                .or()
                .eq(TUser::getGroupId, departmentId)
                .exists();
    }
}
