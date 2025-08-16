package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.mapper.TUserMapper;
import com.giao.devlogpulse.service.ITUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-21
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

}
