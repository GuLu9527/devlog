package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.entity.po.TUserSkill;
import com.giao.devlogpulse.model.dto.UserSkillUpdateDTO;
import com.giao.devlogpulse.service.ITUserSkillService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户技能管理Controller
 */
@RestController
@RequestMapping("/user-skills")
public class UserSkillController {

    @Autowired
    private ITUserSkillService userSkillService;

    /**
     * 获取用户技能列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Map<String, Object>>> getUserSkills(@PathVariable Long userId) {
        List<Map<String, Object>> userSkills = userSkillService.getUserSkillsWithTags(userId);
        return Result.success(userSkills);
    }

    /**
     * 根据标签查询拥有该技能的用户
     */
    @GetMapping("/tag/{tagId}/users")
    public Result<List<Map<String, Object>>> getUsersBySkillTag(@PathVariable Long tagId) {
        List<Map<String, Object>> users = userSkillService.getUsersBySkillTag(tagId);
        return Result.success(users);
    }

    /**
     * 更新用户技能
     */
    @PostMapping("/update")
    public Result<String> updateUserSkills(@Valid @RequestBody UserSkillUpdateDTO updateDTO) {
        try {
            List<TUserSkill> userSkills = updateDTO.getSkills().stream().map(dto -> {
                TUserSkill userSkill = new TUserSkill();
                BeanUtils.copyProperties(dto, userSkill);
                return userSkill;
            }).collect(Collectors.toList());
            
            boolean success = userSkillService.updateUserSkills(updateDTO.getUserId(), userSkills);
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
     * 添加用户技能
     */
    @PostMapping
    public Result<String> addUserSkill(@Valid @RequestBody UserSkillUpdateDTO.UserSkillItemDTO skillDTO,
                                       @RequestParam Long userId) {
        try {
            TUserSkill userSkill = new TUserSkill();
            BeanUtils.copyProperties(skillDTO, userSkill);
            userSkill.setUserId(userId);
            
            boolean success = userSkillService.addUserSkill(userSkill);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.failed("添加失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 删除用户技能
     */
    @DeleteMapping("/user/{userId}/tag/{tagId}")
    public Result<String> removeUserSkill(@PathVariable Long userId, @PathVariable Long tagId) {
        try {
            boolean success = userSkillService.removeUserSkill(userId, tagId);
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
     * 根据技能需求匹配合适的用户
     */
    @PostMapping("/match-users")
    public Result<List<Map<String, Object>>> findMatchingUsers(
            @RequestBody Map<String, Object> params) {
        
        @SuppressWarnings("unchecked")
        List<Long> tagIds = (List<Long>) params.get("tagIds");
        @SuppressWarnings("unchecked")
        List<String> proficiencyLevels = (List<String>) params.get("proficiencyLevels");
        
        List<Map<String, Object>> matchingUsers = userSkillService.findMatchingUsers(tagIds, proficiencyLevels);
        return Result.success(matchingUsers);
    }

    /**
     * 获取用户技能统计
     */
    @GetMapping("/statistics/user/{userId}")
    public Result<List<Map<String, Object>>> getUserSkillStatistics(@PathVariable Long userId) {
        List<Map<String, Object>> statistics = userSkillService.getUserSkillStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取技能熟练度分布
     */
    @GetMapping("/statistics/proficiency-distribution")
    public Result<Map<String, Object>> getSkillProficiencyDistribution() {
        Map<String, Object> distribution = userSkillService.getSkillProficiencyDistribution();
        return Result.success(distribution);
    }

    /**
     * 推荐用户学习的技能
     */
    @GetMapping("/recommendations/user/{userId}")
    public Result<List<Map<String, Object>>> recommendSkillsForUser(@PathVariable Long userId) {
        List<Map<String, Object>> recommendations = userSkillService.recommendSkillsForUser(userId);
        return Result.success(recommendations);
    }

    /**
     * 批量更新用户技能
     */
    @PutMapping("/batch-update")
    public Result<String> batchUpdateUserSkills(@Valid @RequestBody UserSkillUpdateDTO updateDTO) {
        try {
            List<TUserSkill> userSkills = updateDTO.getSkills().stream().map(dto -> {
                TUserSkill userSkill = new TUserSkill();
                BeanUtils.copyProperties(dto, userSkill);
                return userSkill;
            }).collect(Collectors.toList());
            
            boolean success = userSkillService.batchUpdateUserSkills(updateDTO.getUserId(), userSkills);
            if (success) {
                return Result.success("批量更新成功");
            } else {
                return Result.failed("批量更新失败");
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取技能热度排行
     */
    @GetMapping("/statistics/skill-popularity")
    public Result<List<Map<String, Object>>> getSkillPopularity() {
        // TODO: 实现技能热度统计
        return Result.success(null);
    }

    /**
     * 获取团队技能概览
     */
    @GetMapping("/statistics/team-overview")
    public Result<Map<String, Object>> getTeamSkillOverview(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long groupId) {
        // TODO: 实现团队技能概览
        return Result.success(null);
    }
}