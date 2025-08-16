package com.giao.devlogpulse.mapper;

import com.giao.devlogpulse.entity.po.TSkillTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 技术栈标签表 Mapper 接口
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface TSkillTagMapper extends BaseMapper<TSkillTag> {
    
    /**
     * 根据分类查询标签
     */
    @Select("SELECT * FROM t_skill_tag WHERE category = #{category} AND status = 1 ORDER BY sort_order ASC")
    List<TSkillTag> selectByCategory(String category);
    
    /**
     * 查询所有启用的标签
     */
    @Select("SELECT * FROM t_skill_tag WHERE status = 1 ORDER BY category ASC, sort_order ASC")
    List<TSkillTag> selectAllEnabled();
    
    /**
     * 根据名称查询标签（用于检查重复）
     */
    @Select("SELECT COUNT(*) FROM t_skill_tag WHERE tag_name = #{tagName} AND tag_id != #{excludeId}")
    int countByNameExclude(String tagName, Long excludeId);
}