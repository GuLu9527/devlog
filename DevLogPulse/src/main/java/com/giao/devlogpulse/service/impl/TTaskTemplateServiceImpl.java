package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TTaskTemplate;
import com.giao.devlogpulse.mapper.TTaskTemplateMapper;
import com.giao.devlogpulse.service.ITTaskService;
import com.giao.devlogpulse.service.ITTaskTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务模板服务实现类
 */
@Service
public class TTaskTemplateServiceImpl extends ServiceImpl<TTaskTemplateMapper, TTaskTemplate> implements ITTaskTemplateService {

    @Autowired
    private ITTaskService taskService;

    @Override
    @Transactional
    public boolean createTemplate(TTaskTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setIsDeleted(0);
        template.setUsageCount(0);
        
        // 计算任务数量和预计工时
        if (template.getTasks() != null && !template.getTasks().isEmpty()) {
            template.setTasksCount(template.getTasks().size());
            BigDecimal totalHours = template.getTasks().stream()
                    .map(TTaskTemplate.TaskTemplateItem::getEstimatedHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            template.setEstimatedHours(totalHours);
        }
        
        return save(template);
    }

    @Override
    @Transactional
    public List<Long> applyTemplate(Long templateId, Long projectId, Long assigneeId, String startDate, String taskPrefix) {
        TTaskTemplate template = getById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        List<Long> createdTaskIds = new ArrayList<>();
        
        if (template.getTasks() != null && !template.getTasks().isEmpty()) {
            LocalDate baseDate = startDate != null ? LocalDate.parse(startDate) : LocalDate.now();
            
            for (int i = 0; i < template.getTasks().size(); i++) {
                TTaskTemplate.TaskTemplateItem templateItem = template.getTasks().get(i);
                
                TTask task = new TTask();
                task.setTitle(taskPrefix != null ? taskPrefix + templateItem.getTitle() : templateItem.getTitle());
                task.setDescription(templateItem.getDescription());
                task.setPriority(TTask.Priority.valueOf(templateItem.getPriority()));
                task.setProjectId(projectId);
                task.setAssigneeId(assigneeId);
                task.setEstimatedHours(templateItem.getEstimatedHours());
                task.setActualHours(BigDecimal.ZERO);
                task.setProgress(0);
                task.setStatus(TTask.Status.待处理);
                task.setStartTime(baseDate.atStartOfDay());
                task.setCreateTime(LocalDateTime.now());
                task.setUpdateTime(LocalDateTime.now());
                task.setIsDeleted(0);
                task.setDeleteStatus(0);
                
                if (taskService.save(task)) {
                    createdTaskIds.add(task.getId());
                }
            }
        }
        
        // 增加使用次数
        incrementUsageCount(templateId);
        
        return createdTaskIds;
    }

    @Override
    @Transactional
    public TTaskTemplate cloneTemplate(Long templateId, String newName) {
        TTaskTemplate original = getById(templateId);
        if (original == null) {
            throw new RuntimeException("源模板不存在");
        }
        
        TTaskTemplate clone = new TTaskTemplate();
        BeanUtils.copyProperties(original, clone);
        clone.setId(null);
        clone.setName(newName);
        clone.setUsageCount(0);
        clone.setLastUsed(null);
        clone.setCreateTime(LocalDateTime.now());
        clone.setUpdateTime(LocalDateTime.now());
        
        return save(clone) ? clone : null;
    }

    @Override
    public List<TTaskTemplate> getTemplatesByCategory(String category) {
        LambdaQueryWrapper<TTaskTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskTemplate::getCategory, category)
                   .eq(TTaskTemplate::getIsDeleted, 0)
                   .orderByDesc(TTaskTemplate::getUsageCount)
                   .orderByDesc(TTaskTemplate::getCreateTime);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean incrementUsageCount(Long templateId) {
        return baseMapper.incrementUsageCount(templateId) > 0;
    }

    @Override
    @Transactional
    public boolean importTemplate(TTaskTemplate template) {
        template.setId(null);
        template.setUsageCount(0);
        template.setLastUsed(null);
        return createTemplate(template);
    }

    @Override
    public List<TTaskTemplate> getPopularTemplates(int limit) {
        LambdaQueryWrapper<TTaskTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskTemplate::getIsDeleted, 0)
                   .orderByDesc(TTaskTemplate::getUsageCount)
                   .orderByDesc(TTaskTemplate::getCreateTime)
                   .last("LIMIT " + limit);
        
        return list(queryWrapper);
    }
}