package com.giao.devlogpulse.mapper;

import com.giao.devlogpulse.entity.po.TUserSkill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户技能关联表 Mapper 接口
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface TUserSkillMapper extends BaseMapper<TUserSkill> {
    
    /**
     * 根据用户ID查询技能列表（包含标签信息）
     */
    @Select("SELECT us.*, st.tag_name, st.description, st.color, st.category " +
            "FROM t_user_skill us " +
            "LEFT JOIN t_skill_tag st ON us.tag_id = st.tag_id " +
            "WHERE us.user_id = #{userId} " +
            "ORDER BY us.is_primary DESC, us.self_rating DESC")
    List<Map<String, Object>> selectUserSkillsWithTags(Long userId);
    
    /**
     * 根据标签ID查询拥有该技能的用户列表
     */
    @Select("SELECT us.*, tu.username, tu.real_name, tu.email " +
            "FROM t_user_skill us " +
            "LEFT JOIN t_user tu ON us.user_id = tu.id " +
            "WHERE us.tag_id = #{tagId} " +
            "ORDER BY us.proficiency_level DESC, us.self_rating DESC")
    List<Map<String, Object>> selectUsersBySkillTag(Long tagId);
    
    /**
     * 删除用户的所有技能
     */
    @Delete("DELETE FROM t_user_skill WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
    
    /**
     * 批量插入用户技能
     */
    void insertBatch(@Param("userSkills") List<TUserSkill> userSkills);
    
    /**
     * 根据技能需求匹配合适的用户
     */
    @Select("SELECT us.user_id, tu.username, tu.real_name, " +
            "COUNT(*) as matched_skills, " +
            "AVG(us.self_rating) as avg_rating " +
            "FROM t_user_skill us " +
            "LEFT JOIN t_user tu ON us.user_id = tu.id " +
            "WHERE us.tag_id IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach> " +
            "AND us.proficiency_level IN " +
            "<foreach collection='proficiencyLevels' item='level' open='(' separator=',' close=')'>" +
            "#{level}" +
            "</foreach> " +
            "GROUP BY us.user_id, tu.username, tu.real_name " +
            "ORDER BY matched_skills DESC, avg_rating DESC")
    List<Map<String, Object>> findMatchingUsers(@Param("tagIds") List<Long> tagIds, 
                                               @Param("proficiencyLevels") List<String> proficiencyLevels);
}