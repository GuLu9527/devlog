import request from '@/utils/request'

/**
 * 创建工时记录
 * @param {Object} data 工时记录数据
 */
export const createWorkHour = (data) => {
  return request({
    url: '/work-hours',
    method: 'post',
    data
  })
}

/**
 * 更新工时记录
 * @param {number} id 工时记录ID
 * @param {Object} data 工时记录数据
 */
export const updateWorkHour = (id, data) => {
  return request({
    url: `/work-hours/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除工时记录
 * @param {number} id 工时记录ID
 */
export const deleteWorkHour = (id) => {
  return request({
    url: `/work-hours/${id}`,
    method: 'delete'
  })
}

/**
 * 获取用户工时记录
 * @param {Object} params 查询参数
 */
export const getUserWorkHours = (params) => {
  return request({
    url: '/work-hours/user',
    method: 'get',
    params
  })
}

/**
 * 获取项目工时统计
 * @param {number} projectId 项目ID
 * @param {Object} params 查询参数
 */
export const getProjectWorkHourStats = (projectId, params) => {
  return request({
    url: `/work-hours/project/${projectId}/stats`,
    method: 'get',
    params
  })
}

/**
 * 获取任务工时统计
 * @param {number} taskId 任务ID
 */
export const getTaskWorkHourStats = (taskId) => {
  return request({
    url: `/work-hours/task/${taskId}/stats`,
    method: 'get'
  })
}

/**
 * 审核工时记录
 * @param {number} id 工时记录ID
 * @param {Object} data 审核数据
 */
export const reviewWorkHour = (id, data) => {
  return request({
    url: `/work-hours/${id}/review`,
    method: 'post',
    data
  })
}

/**
 * 获取待审核工时记录
 * @param {Object} params 查询参数
 */
export const getPendingWorkHours = (params) => {
  return request({
    url: '/work-hours/pending',
    method: 'get',
    params
  })
}

/**
 * 批量审核工时记录
 * @param {Object} data 批量审核数据
 */
export const batchReviewWorkHours = (data) => {
  return request({
    url: '/work-hours/batch-review',
    method: 'post',
    data
  })
}

/**
 * 获取用户工时统计
 * @param {number} userId 用户ID
 * @param {Object} params 查询参数
 */
export const getUserWorkHourStats = (userId, params) => {
  return request({
    url: `/work-hours/user/${userId}/stats`,
    method: 'get',
    params
  })
}

/**
 * 获取团队工时排行
 * @param {Object} params 查询参数
 */
export const getTeamWorkHourRanking = (params) => {
  return request({
    url: '/work-hours/team/ranking',
    method: 'get',
    params
  })
}

/**
 * 获取用户日工时汇总
 * @param {number} userId 用户ID
 * @param {string} date 日期
 */
export const getUserDailyWorkHours = (userId, date) => {
  return request({
    url: `/work-hours/user/${userId}/daily`,
    method: 'get',
    params: { date }
  })
}