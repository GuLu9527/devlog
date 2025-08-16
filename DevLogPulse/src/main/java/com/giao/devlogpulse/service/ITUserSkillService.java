package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TUserSkill;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户技能关联表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface ITUserSkillService extends IService<TUserSkill> {
    
    /**
     * 获取用户技能列表（包含标签信息）
     */
    List<Map<String, Object>> getUserSkillsWithTags(Long userId);
    
    /**
     * 根据标签查询拥有该技能的用户
     */
    List<Map<String, Object>> getUsersBySkillTag(Long tagId);
    
    /**
     * 更新用户技能
     */
    boolean updateUserSkills(Long userId, List<TUserSkill> userSkills);
    
    /**
     * 添加用户技能
     */
    boolean addUserSkill(TUserSkill userSkill);
    
    /**
     * 删除用户技能
     */
    boolean removeUserSkill(Long userId, Long tagId);
    
    /**
     * 批量更新用户技能
     */
    boolean batchUpdateUserSkills(Long userId, List<TUserSkill> userSkills);
    
    /**
     * 根据技能需求匹配合适的用户
     */
    List<Map<String, Object>> findMatchingUsers(List<Long> tagIds, List<String> proficiencyLevels);
    
    /**
     * 获取用户技能分布统计
     */
    List<Map<String, Object>> getUserSkillStatistics(Long userId);
    
    /**
     * 获取技能熟练度分布
     */
    Map<String, Object> getSkillProficiencyDistribution();
    
    /**
     * 推荐用户学习的技能
     */
    List<Map<String, Object>> recommendSkillsForUser(Long userId);
}