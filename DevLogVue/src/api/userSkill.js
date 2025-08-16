import request from '@/utils/request'

/**
 * 用户技能API
 */

// 获取用户技能列表
export function getUserSkills(userId) {
  return request({
    url: `/user-skills/user/${userId}`,
    method: 'get'
  })
}

// 根据标签查询拥有该技能的用户
export function getUsersBySkillTag(tagId) {
  return request({
    url: `/user-skills/tag/${tagId}/users`,
    method: 'get'
  })
}

// 更新用户技能
export function updateUserSkills(data) {
  return request({
    url: '/user-skills/update',
    method: 'post',
    data
  })
}

// 添加用户技能
export function addUserSkill(data, userId) {
  return request({
    url: '/user-skills',
    method: 'post',
    data,
    params: { userId }
  })
}

// 删除用户技能
export function removeUserSkill(userId, tagId) {
  return request({
    url: `/user-skills/user/${userId}/tag/${tagId}`,
    method: 'delete'
  })
}

// 根据技能需求匹配合适的用户
export function findMatchingUsers(params) {
  return request({
    url: '/user-skills/match-users',
    method: 'post',
    data: params
  })
}

// 获取用户技能统计
export function getUserSkillStatistics(userId) {
  return request({
    url: `/user-skills/statistics/user/${userId}`,
    method: 'get'
  })
}

// 获取技能熟练度分布
export function getSkillProficiencyDistribution() {
  return request({
    url: '/user-skills/statistics/proficiency-distribution',
    method: 'get'
  })
}

// 推荐用户学习的技能
export function recommendSkillsForUser(userId) {
  return request({
    url: `/user-skills/recommendations/user/${userId}`,
    method: 'get'
  })
}

// 批量更新用户技能
export function batchUpdateUserSkills(data) {
  return request({
    url: '/user-skills/batch-update',
    method: 'put',
    data
  })
}

// 获取技能热度排行
export function getSkillPopularity() {
  return request({
    url: '/user-skills/statistics/skill-popularity',
    method: 'get'
  })
}

// 获取团队技能概览
export function getTeamSkillOverview(params) {
  return request({
    url: '/user-skills/statistics/team-overview',
    method: 'get',
    params
  })
}