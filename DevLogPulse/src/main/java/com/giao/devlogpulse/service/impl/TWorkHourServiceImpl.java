package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TWorkHour;
import com.giao.devlogpulse.mapper.TWorkHourMapper;
import com.giao.devlogpulse.service.ITWorkHourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工时记录服务实现类
 */
@Service
public class TWorkHourServiceImpl extends ServiceImpl<TWorkHourMapper, TWorkHour> implements ITWorkHourService {

    @Override
    @Transactional
    //未来希望这里直接把错误信息返回前端
    public boolean createWorkHour(TWorkHour workHour) {
        // 检查是否已存在同用户同任务同日期的记录
        boolean exists = checkWorkHourConflict(workHour.getUserId(), workHour.getTaskId(), workHour.getWorkDate());
        if (exists) {
            throw new RuntimeException("该任务在此日期已有工时记录");
        }
        
        workHour.setStatus(TWorkHour.Status.PENDING);
        workHour.setCreateTime(LocalDateTime.now());
        workHour.setUpdateTime(LocalDateTime.now());
        workHour.setIsDeleted(0);
        
        return save(workHour);
    }

    @Override
    public Page<TWorkHour> getUserWorkHours(Long userId, LocalDate startDate, LocalDate endDate, String status, Integer pageNum, Integer pageSize) {
        Page<TWorkHour> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TWorkHour> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(TWorkHour::getUserId, userId)
                   .eq(TWorkHour::getIsDeleted, 0);
        
        if (startDate != null) {
            queryWrapper.ge(TWorkHour::getWorkDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(TWorkHour::getWorkDate, endDate);
        }
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq(TWorkHour::getStatus, TWorkHour.Status.valueOf(status));
        }
        
        queryWrapper.orderByDesc(TWorkHour::getWorkDate)
                   .orderByDesc(TWorkHour::getCreateTime);
        
        return page(page, queryWrapper);
    }

    @Override
    public Map<String, Object> getProjectWorkHourStats(Long projectId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.getProjectWorkHourStats(projectId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getTaskWorkHourStats(Long taskId) {
        return baseMapper.getTaskWorkHourStats(taskId);
    }

    @Override
    @Transactional
    public boolean reviewWorkHour(Long workHourId, TWorkHour.Status status, String reviewNote, Long reviewerId) {
        TWorkHour workHour = getById(workHourId);
        if (workHour == null || workHour.getIsDeleted() == 1) {
            return false;
        }
        
        workHour.setStatus(status);
        workHour.setReviewNote(reviewNote);
        workHour.setReviewerId(reviewerId);
        workHour.setReviewTime(LocalDateTime.now());
        workHour.setUpdateTime(LocalDateTime.now());
        
        return updateById(workHour);
    }

    @Override
    public Page<TWorkHour> getPendingWorkHours(Integer pageNum, Integer pageSize) {
        Page<TWorkHour> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TWorkHour> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(TWorkHour::getStatus, TWorkHour.Status.PENDING)
                   .eq(TWorkHour::getIsDeleted, 0)
                   .orderByAsc(TWorkHour::getCreateTime);
        
        return page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean batchReviewWorkHours(List<Long> workHourIds, TWorkHour.Status status, String reviewNote, Long reviewerId) {
        LambdaQueryWrapper<TWorkHour> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TWorkHour::getId, workHourIds)
                   .eq(TWorkHour::getIsDeleted, 0);
        
        TWorkHour updateEntity = new TWorkHour();
        updateEntity.setStatus(status);
        updateEntity.setReviewNote(reviewNote);
        updateEntity.setReviewerId(reviewerId);
        updateEntity.setReviewTime(LocalDateTime.now());
        updateEntity.setUpdateTime(LocalDateTime.now());
        
        return update(updateEntity, queryWrapper);
    }

    @Override
    public Map<String, Object> getUserWorkHourStats(Long userId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.getUserWorkHourStats(userId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getTeamWorkHourRanking(LocalDate startDate, LocalDate endDate, int limit) {
        return baseMapper.getTeamWorkHourRanking(startDate, endDate, limit);
    }

    @Override
    public boolean checkWorkHourConflict(Long userId, Long taskId, LocalDate workDate) {
        LambdaQueryWrapper<TWorkHour> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TWorkHour::getUserId, userId)
                   .eq(TWorkHour::getTaskId, taskId)
                   .eq(TWorkHour::getWorkDate, workDate)
                   .eq(TWorkHour::getIsDeleted, 0);
        
        return count(queryWrapper) > 0;
    }

    @Override
    public BigDecimal getUserDailyWorkHours(Long userId, LocalDate date) {
        LambdaQueryWrapper<TWorkHour> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TWorkHour::getUserId, userId)
                   .eq(TWorkHour::getWorkDate, date)
                   .eq(TWorkHour::getIsDeleted, 0)
                   .in(TWorkHour::getStatus, TWorkHour.Status.PENDING, TWorkHour.Status.APPROVED);
        
        List<TWorkHour> workHours = list(queryWrapper);
        return workHours.stream()
                .map(TWorkHour::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}