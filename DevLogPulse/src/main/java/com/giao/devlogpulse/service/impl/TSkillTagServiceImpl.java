package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TSkillTag;
import com.giao.devlogpulse.entity.po.TUserSkill;
import com.giao.devlogpulse.entity.po.TTaskRequirement;
import com.giao.devlogpulse.mapper.TSkillTagMapper;
import com.giao.devlogpulse.mapper.TUserSkillMapper;
import com.giao.devlogpulse.mapper.TTaskRequirementMapper;
import com.giao.devlogpulse.service.ITSkillTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 技术栈标签表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Service
public class TSkillTagServiceImpl extends ServiceImpl<TSkillTagMapper, TSkillTag> implements ITSkillTagService {

    @Autowired
    private TUserSkillMapper userSkillMapper;

    @Autowired
    private TTaskRequirementMapper taskRequirementMapper;

    @Override
    public List<TSkillTag> getByCategory(String category) {
        return baseMapper.selectByCategory(category);
    }

    @Override
    public List<TSkillTag> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public boolean checkNameExists(String tagName, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L;
        }
        return baseMapper.countByNameExclude(tagName, excludeId) > 0;
    }

    @Override
    @Transactional
    public boolean createTag(TSkillTag skillTag) {
        // 检查名称重复
        if (checkNameExists(skillTag.getTagName(), null)) {
            throw new RuntimeException("标签名称已存在");
        }
        
        skillTag.setCreateTime(LocalDateTime.now());
        skillTag.setUpdateTime(LocalDateTime.now());
        if (skillTag.getStatus() == null) {
            skillTag.setStatus(1);
        }
        if (skillTag.getSortOrder() == null) {
            skillTag.setSortOrder(0);
        }
        
        return save(skillTag);
    }

    @Override
    @Transactional
    public boolean updateTag(TSkillTag skillTag) {
        // 检查名称重复
        if (checkNameExists(skillTag.getTagName(), skillTag.getTagId())) {
            throw new RuntimeException("标签名称已存在");
        }
        
        skillTag.setUpdateTime(LocalDateTime.now());
        return updateById(skillTag);
    }

    @Override
    @Transactional
    public boolean deleteTag(Long tagId) {
        // 检查是否被用户技能使用
        QueryWrapper<TUserSkill> userSkillQuery = new QueryWrapper<>();
        userSkillQuery.eq("tag_id", tagId);
        Long userSkillCount = userSkillMapper.selectCount(userSkillQuery);
        if (userSkillCount > 0) {
            throw new RuntimeException("该标签正被用户技能使用，无法删除");
        }
        
        // 检查是否被任务需求使用
        QueryWrapper<TTaskRequirement> taskReqQuery = new QueryWrapper<>();
        taskReqQuery.eq("required_tag_id", tagId);
        Long taskReqCount = taskRequirementMapper.selectCount(taskReqQuery);
        if (taskReqCount > 0) {
            throw new RuntimeException("该标签正被任务需求使用，无法删除");
        }
        
        return removeById(tagId);
    }

    @Override
    @Transactional
    public boolean batchImport(List<TSkillTag> tags) {
        LocalDateTime now = LocalDateTime.now();
        for (TSkillTag tag : tags) {
            if (checkNameExists(tag.getTagName(), null)) {
                continue; // 跳过重复的标签
            }
            tag.setCreateTime(now);
            tag.setUpdateTime(now);
            if (tag.getStatus() == null) {
                tag.setStatus(1);
            }
            if (tag.getSortOrder() == null) {
                tag.setSortOrder(0);
            }
        }
        return saveBatch(tags);
    }

    @Override
    public List<Map<String, Object>> getUsageStatistics() {
        // TODO: 实现标签使用统计查询
        // 这里需要复杂的SQL查询，统计每个标签被多少用户掌握、多少任务需要等
        return null;
    }
}