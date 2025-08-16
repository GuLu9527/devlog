package com.giao.devlogpulse.mapper;

import com.giao.devlogpulse.entity.po.TTaskRequirement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务技能需求关联表 Mapper 接口
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface TTaskRequirementMapper extends BaseMapper<TTaskRequirement> {
    
    /**
     * 根据任务ID查询需求列表（包含标签信息）
     */
    @Select("SELECT tr.*, st.tag_name, st.description, st.color, st.category " +
            "FROM t_task_requirement tr " +
            "LEFT JOIN t_skill_tag st ON tr.required_tag_id = st.tag_id " +
            "WHERE tr.task_id = #{taskId} " +
            "ORDER BY tr.is_required DESC, tr.importance DESC")
    List<Map<String, Object>> selectTaskRequirementsWithTags(Long taskId);
    
    /**
     * 根据标签ID查询需要该技能的任务列表
     */
    @Select("SELECT tr.*, tt.title, tt.description, tt.status, tt.priority " +
            "FROM t_task_requirement tr " +
            "LEFT JOIN t_task tt ON tr.task_id = tt.id " +
            "WHERE tr.required_tag_id = #{tagId} " +
            "ORDER BY tr.importance DESC")
    List<Map<String, Object>> selectTasksBySkillTag(Long tagId);
    
    /**
     * 删除任务的所有技能需求
     */
    @Delete("DELETE FROM t_task_requirement WHERE task_id = #{taskId}")
    void deleteByTaskId(Long taskId);
    
    /**
     * 批量插入任务需求
     */
    void insertBatch(@Param("requirements") List<TTaskRequirement> requirements);
    
    /**
     * 根据用户技能匹配适合的任务
     */
    @Select("SELECT tr.task_id, tt.title, tt.description, tt.priority, " +
            "COUNT(*) as required_skills, " +
            "SUM(CASE WHEN us.user_id IS NOT NULL THEN 1 ELSE 0 END) as matched_skills, " +
            "ROUND(SUM(CASE WHEN us.user_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as match_percentage " +
            "FROM t_task_requirement tr " +
            "LEFT JOIN t_task tt ON tr.task_id = tt.id " +
            "LEFT JOIN t_user_skill us ON tr.required_tag_id = us.tag_id AND us.user_id = #{userId} " +
            "WHERE tt.status NOT IN ('已完成', '已取消') " +
            "GROUP BY tr.task_id, tt.title, tt.description, tt.priority " +
            "HAVING matched_skills > 0 " +
            "ORDER BY match_percentage DESC, matched_skills DESC")
    List<Map<String, Object>> findSuitableTasksForUser(Long userId);
    
    /**
     * 获取任务技能需求统计
     */
    @Select("SELECT st.tag_name, st.category, COUNT(*) as task_count, " +
            "AVG(tr.importance) as avg_importance " +
            "FROM t_task_requirement tr " +
            "LEFT JOIN t_skill_tag st ON tr.required_tag_id = st.tag_id " +
            "LEFT JOIN t_task tt ON tr.task_id = tt.id " +
            "WHERE tt.status NOT IN ('已完成', '已取消') " +
            "GROUP BY st.tag_id, st.tag_name, st.category " +
            "ORDER BY task_count DESC")
    List<Map<String, Object>> getSkillDemandStatistics();
}