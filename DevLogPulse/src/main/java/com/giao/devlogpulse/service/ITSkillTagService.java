package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TSkillTag;
import java.util.List;

/**
 * <p>
 * 技术栈标签表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface ITSkillTagService extends IService<TSkillTag> {
    
    /**
     * 根据分类查询标签
     */
    List<TSkillTag> getByCategory(String category);
    
    /**
     * 查询所有启用的标签
     */
    List<TSkillTag> getAllEnabled();
    
    /**
     * 检查标签名称是否重复
     */
    boolean checkNameExists(String tagName, Long excludeId);
    
    /**
     * 创建标签
     */
    boolean createTag(TSkillTag skillTag);
    
    /**
     * 更新标签
     */
    boolean updateTag(TSkillTag skillTag);
    
    /**
     * 删除标签（检查是否被使用）
     */
    boolean deleteTag(Long tagId);
    
    /**
     * 批量导入标签
     */
    boolean batchImport(List<TSkillTag> tags);
    
    /**
     * 获取标签使用统计
     */
    List<java.util.Map<String, Object>> getUsageStatistics();
}