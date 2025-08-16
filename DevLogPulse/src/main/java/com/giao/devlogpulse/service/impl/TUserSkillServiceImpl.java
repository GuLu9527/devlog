package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TUserSkill;
import com.giao.devlogpulse.mapper.TUserSkillMapper;
import com.giao.devlogpulse.service.ITUserSkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * <p>
 * 用户技能关联表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Service
public class TUserSkillServiceImpl extends ServiceImpl<TUserSkillMapper, TUserSkill> implements ITUserSkillService {

    @Override
    public List<Map<String, Object>> getUserSkillsWithTags(Long userId) {
        return baseMapper.selectUserSkillsWithTags(userId);
    }

    @Override
    public List<Map<String, Object>> getUsersBySkillTag(Long tagId) {
        return baseMapper.selectUsersBySkillTag(tagId);
    }

    @Override
    @Transactional
    public boolean updateUserSkills(Long userId, List<TUserSkill> userSkills) {
        // 先删除用户现有的所有技能
        baseMapper.deleteByUserId(userId);
        
        // 批量插入新的技能
        if (userSkills != null && !userSkills.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (TUserSkill skill : userSkills) {
                skill.setUserId(userId);
                skill.setCreateTime(now);
                skill.setUpdateTime(now);
                if (skill.getIsPrimary() == null) {
                    skill.setIsPrimary(0);
                }
                if (skill.getSelfRating() == null) {
                    skill.setSelfRating(5);
                }
            }
            baseMapper.insertBatch(userSkills);
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean addUserSkill(TUserSkill userSkill) {
        // 检查是否已存在
        QueryWrapper<TUserSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userSkill.getUserId())
                   .eq("tag_id", userSkill.getTagId());
        TUserSkill existing = getOne(queryWrapper);
        
        if (existing != null) {
            // 更新现有记录
            existing.setProficiencyLevel(userSkill.getProficiencyLevel());
            existing.setYearsOfExperience(userSkill.getYearsOfExperience());
            existing.setLastUsed(userSkill.getLastUsed());
            existing.setIsPrimary(userSkill.getIsPrimary());
            existing.setSelfRating(userSkill.getSelfRating());
            existing.setRemark(userSkill.getRemark());
            existing.setUpdateTime(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 新增记录
            userSkill.setCreateTime(LocalDateTime.now());
            userSkill.setUpdateTime(LocalDateTime.now());
            if (userSkill.getIsPrimary() == null) {
                userSkill.setIsPrimary(0);
            }
            if (userSkill.getSelfRating() == null) {
                userSkill.setSelfRating(5);
            }
            return save(userSkill);
        }
    }

    @Override
    @Transactional
    public boolean removeUserSkill(Long userId, Long tagId) {
        QueryWrapper<TUserSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("tag_id", tagId);
        return remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean batchUpdateUserSkills(Long userId, List<TUserSkill> userSkills) {
        return updateUserSkills(userId, userSkills);
    }

    @Override
    public List<Map<String, Object>> findMatchingUsers(List<Long> tagIds, List<String> proficiencyLevels) {
        if (proficiencyLevels == null || proficiencyLevels.isEmpty()) {
            proficiencyLevels = Arrays.asList("初级", "中级", "高级", "专家");
        }
        return baseMapper.findMatchingUsers(tagIds, proficiencyLevels);
    }

    @Override
    public List<Map<String, Object>> getUserSkillStatistics(Long userId) {
        // TODO: 实现用户技能统计
        return null;
    }

    @Override
    public Map<String, Object> getSkillProficiencyDistribution() {
        // TODO: 实现技能熟练度分布统计
        return null;
    }

    @Override
    public List<Map<String, Object>> recommendSkillsForUser(Long userId) {
        // TODO: 实现技能推荐算法
        return null;
    }
}