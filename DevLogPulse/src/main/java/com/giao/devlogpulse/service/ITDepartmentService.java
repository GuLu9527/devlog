package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TDepartment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
public interface ITDepartmentService extends IService<TDepartment> {

    /**
     * 检查部门下是否有用户
     *
     * @param departmentId 部门ID
     * @return 是否有用户
     */
    boolean hasUsers(Long departmentId);
}
