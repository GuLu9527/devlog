package com.giao.devlogpulse.service.impl;

import com.giao.devlogpulse.entity.po.TOperationLog;
import com.giao.devlogpulse.mapper.TOperationLogMapper;
import com.giao.devlogpulse.service.ITOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Service
public class TOperationLogServiceImpl extends ServiceImpl<TOperationLogMapper, TOperationLog> implements ITOperationLogService {

}
