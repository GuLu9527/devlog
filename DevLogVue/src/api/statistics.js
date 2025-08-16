import request from '@/utils/request'

/**
 * 获取工时统计数据
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} params.endDate - 结束日期 (YYYY-MM-DD) 
 * @param {number} params.userId - 用户ID
 * @param {number} params.groupId - 组ID
 * @param {number} params.projectId - 项目ID
 * @param {string} params.dimension - 统计维度 (day/week/month)
 */
export const getWorkHoursStatistics = (params) => request.get('/statistics/workhours', { params })

/**
 * 获取任务统计数据
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} params.endDate - 结束日期 (YYYY-MM-DD)
 * @param {number} params.userId - 用户ID
 * @param {number} params.groupId - 组ID  
 * @param {number} params.projectId - 项目ID
 */
export const getTaskStatistics = (params) => request.get('/statistics/tasks', { params })

/**
 * 获取项目进度统计数据
 * @param {Object} params - 查询参数
 * @param {number} params.projectId - 项目ID
 * @param {number} params.departmentId - 部门ID
 */
export const getProjectStatistics = (params) => request.get('/statistics/projects', { params })

/**
 * 获取个人工作概览
 * @param {number} userId - 用户ID，不传则使用当前登录用户
 */
export const getPersonalOverview = (userId) => request.get('/statistics/personal-overview', { 
  params: userId ? { userId } : {} 
})

/**
 * 获取团队工作概览  
 * @param {Object} params - 查询参数
 * @param {number} params.groupId - 组ID
 * @param {number} params.departmentId - 部门ID
 */
export const getTeamOverview = (params) => request.get('/statistics/team-overview', { params })

/**
 * 获取首页仪表板数据
 */
export const getDashboardData = () => request.get('/statistics/dashboard')