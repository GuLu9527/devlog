package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TSkillTag;
import com.giao.devlogpulse.model.dto.SkillTagCreateDTO;
import com.giao.devlogpulse.model.vo.SkillTagVO;
import com.giao.devlogpulse.service.ITSkillTagService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技能标签管理Controller
 */
@RestController
@RequestMapping("/skill-tags")
public class SkillTagController {

    @Autowired
    private ITSkillTagService skillTagService;

    /**
     * 分页查询标签列表
     */
    @GetMapping
    @SaCheckPermission("skilltag:list")
    public Result<IPage<SkillTagVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        Page<TSkillTag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TSkillTag> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (StringUtils.hasText(tagName)) {
            queryWrapper.like(TSkillTag::getTagName, tagName);
        }
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(TSkillTag::getCategory, category);
        }
        if (status != null) {
            queryWrapper.eq(TSkillTag::getStatus, status);
        }
        
        queryWrapper.orderByAsc(TSkillTag::getCategory)
                   .orderByAsc(TSkillTag::getSortOrder)
                   .orderByDesc(TSkillTag::getCreateTime);
        
        IPage<TSkillTag> pageResult = skillTagService.page(page, queryWrapper);
        
        // 转换为VO
        IPage<SkillTagVO> voPage = pageResult.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    /**
     * 根据分类查询标签
     */
    @GetMapping("/category/{category}")
    @SaCheckPermission("skilltag:list")
    public Result<List<SkillTagVO>> getByCategory(@PathVariable String category) {
        List<TSkillTag> tags = skillTagService.getByCategory(category);
        List<SkillTagVO> voList = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 查询所有启用的标签
     */
    @GetMapping("/enabled")
    @SaCheckPermission("skilltag:list")
    public Result<List<SkillTagVO>> getAllEnabled() {
        List<TSkillTag> tags = skillTagService.getAllEnabled();
        List<SkillTagVO> voList = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("skilltag:read")
    public Result<SkillTagVO> getById(@PathVariable Long id) {
        TSkillTag tag = skillTagService.getById(id);
        if (tag == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "标签不存在");
        }
        return Result.success(convertToVO(tag));
    }

    /**
     * 创建标签
     */
    @PostMapping
    @SaCheckPermission("skilltag:create")
    public Result<String> create(@Valid @RequestBody SkillTagCreateDTO createDTO) {
        try {
            TSkillTag tag = new TSkillTag();
            BeanUtils.copyProperties(createDTO, tag);
            tag.setCreatedBy(getCurrentUserId()); // TODO: 从当前登录用户获取
            
            boolean success = skillTagService.createTag(tag);
            if (success) {
                return Result.success("创建成功");
            } else {
                return Result.failed("创建失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    @SaCheckPermission("skilltag:update")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody SkillTagCreateDTO updateDTO) {
        try {
            TSkillTag tag = skillTagService.getById(id);
            if (tag == null) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "标签不存在");
            }
            
            BeanUtils.copyProperties(updateDTO, tag);
            tag.setTagId(id);
            tag.setUpdatedBy(getCurrentUserId()); // TODO: 从当前登录用户获取
            
            boolean success = skillTagService.updateTag(tag);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.failed("更新失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("skilltag:delete")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = skillTagService.deleteTag(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.failed("删除失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 批量删除标签
     */
    @DeleteMapping("/batch")
    @SaCheckPermission("skilltag:delete")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                skillTagService.deleteTag(id);
            }
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 批量导入标签
     */
    @PostMapping("/batch-import")
    @SaCheckPermission("skilltag:create")
    public Result<String> batchImport(@Valid @RequestBody List<SkillTagCreateDTO> tagList) {
        try {
            List<TSkillTag> tags = tagList.stream().map(dto -> {
                TSkillTag tag = new TSkillTag();
                BeanUtils.copyProperties(dto, tag);
                tag.setCreatedBy(getCurrentUserId());
                return tag;
            }).collect(Collectors.toList());
            
            boolean success = skillTagService.batchImport(tags);
            if (success) {
                return Result.success("批量导入成功");
            } else {
                return Result.failed("批量导入失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取标签使用统计
     */
    @GetMapping("/statistics")
    @SaCheckPermission("skilltag:list")
    public Result<List<Map<String, Object>>> getUsageStatistics() {
        List<Map<String, Object>> statistics = skillTagService.getUsageStatistics();
        return Result.success(statistics);
    }

    /**
     * 检查标签名称是否存在
     */
    @GetMapping("/check-name")
    @SaCheckPermission("skilltag:read")
    public Result<Boolean> checkNameExists(
            @RequestParam String tagName,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = skillTagService.checkNameExists(tagName, excludeId);
        return Result.success(exists);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    @SaCheckPermission("skilltag:list")
    public Result<List<String>> getCategories() {
        LambdaQueryWrapper<TSkillTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TSkillTag::getCategory)
                   .eq(TSkillTag::getStatus, 1)
                   .groupBy(TSkillTag::getCategory)
                   .orderByAsc(TSkillTag::getCategory);
        
        List<TSkillTag> tags = skillTagService.list(queryWrapper);
        List<String> categories = tags.stream()
                .map(TSkillTag::getCategory)
                .distinct()
                .collect(Collectors.toList());
        
        return Result.success(categories);
    }

    /**
     * 转换为VO
     */
    private SkillTagVO convertToVO(TSkillTag tag) {
        SkillTagVO vo = new SkillTagVO();
        BeanUtils.copyProperties(tag, vo);
        // TODO: 可以在这里添加统计信息，如用户数、任务数等
        return vo;
    }

    /**
     * 获取当前用户ID
     * TODO: 实现从JWT或Session中获取当前用户ID
     */
    private Long getCurrentUserId() {
        return 1L; // 临时返回固定值
    }
}