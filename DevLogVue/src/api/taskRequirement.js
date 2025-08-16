import request from '@/utils/request'

/**
 * 任务技能需求API
 */

// 获取任务技能需求列表
export function getTaskRequirements(taskId) {
  return request({
    url: `/task-requirements/task/${taskId}`,
    method: 'get'
  })
}

// 根据标签查询需要该技能的任务
export function getTasksBySkillTag(tagId) {
  return request({
    url: `/task-requirements/tag/${tagId}/tasks`,
    method: 'get'
  })
}

// 更新任务技能需求
export function updateTaskRequirements(data) {
  return request({
    url: '/task-requirements/update',
    method: 'post',
    data
  })
}

// 添加任务技能需求
export function addTaskRequirement(data, taskId) {
  return request({
    url: '/task-requirements',
    method: 'post',
    data,
    params: { taskId }
  })
}

// 删除任务技能需求
export function removeTaskRequirement(taskId, tagId) {
  return request({
    url: `/task-requirements/task/${taskId}/tag/${tagId}`,
    method: 'delete'
  })
}

// 根据用户技能匹配适合的任务
export function findSuitableTasksForUser(userId) {
  return request({
    url: `/task-requirements/suitable-tasks/user/${userId}`,
    method: 'get'
  })
}

// 获取技能需求统计
export function getSkillDemandStatistics() {
  return request({
    url: '/task-requirements/statistics/skill-demand',
    method: 'get'
  })
}

// 获取任务匹配度分析
export function getTaskMatchAnalysis(taskId, userId) {
  return request({
    url: `/task-requirements/analysis/task/${taskId}/user/${userId}`,
    method: 'get'
  })
}

// 推荐任务给用户
export function recommendTasksForUser(userId, limit = 10) {
  return request({
    url: `/task-requirements/recommendations/user/${userId}`,
    method: 'get',
    params: { limit }
  })
}

// 获取项目技能需求概览
export function getProjectSkillOverview(projectId) {
  return request({
    url: `/task-requirements/statistics/project/${projectId}/skill-overview`,
    method: 'get'
  })
}

// 批量更新任务需求
export function batchUpdateTaskRequirements(data) {
  return request({
    url: '/task-requirements/batch-update',
    method: 'put',
    data
  })
}

// 获取技能需求热力图数据
export function getSkillDemandHeatmap(params) {
  return request({
    url: '/task-requirements/statistics/skill-heatmap',
    method: 'get',
    params
  })
}

// 获取任务分配建议
export function getTaskAssignmentSuggestions(taskId) {
  return request({
    url: `/task-requirements/suggestions/task/${taskId}`,
    method: 'get'
  })
}

// 获取技能缺口分析
export function getSkillGapAnalysis(params) {
  return request({
    url: '/task-requirements/analysis/skill-gap',
    method: 'get',
    params
  })
}