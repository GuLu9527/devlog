import request from "@/utils/request";

/**
 * 获取工作日志列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 页大小
 * @param {number} params.taskId - 任务ID
 * @param {number} params.userId - 提交人ID
 * @param {number} params.groupId - 小组ID
 * @param {number} params.reviewerId - 审核人ID
 * @param {string} params.reviewStatus - 审核状态
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 * @param {string} params.content - 内容关键词
 */
export const getWorkLogList = (params) => request.get('/worklogs', { params });

/**
 * 获取工作日志详情
 * @param {number} id - 工作日志ID
 */
export const getWorkLogDetail = (id) => request.get(`/worklogs/${id}`);

/**
 * 创建工作日志
 * @param {Object} data - 工作日志信息
 * @param {number} data.taskId - 任务ID
 * @param {string} data.content - 日志内容
 * @param {number} data.hours - 工时
 * @param {string} data.logDate - 日志日期
 * @param {number} data.groupId - 小组ID
 */
export const createWorkLog = (data) => request.post('/worklogs', data);

/**
 * 更新工作日志
 * @param {number} id - 工作日志ID
 * @param {Object} data - 更新的工作日志信息
 */
export const updateWorkLog = (id, data) => request.put(`/worklogs/${id}`, data);

/**
 * 删除工作日志
 * @param {number} id - 工作日志ID
 */
export const deleteWorkLog = (id) => request.delete(`/worklogs/${id}`);

/**
 * 审核工作日志
 * @param {number} id - 工作日志ID
 * @param {Object} data - 审核信息
 * @param {string} data.reviewStatus - 审核状态
 * @param {string} data.reviewComment - 审核意见
 */
export const reviewWorkLog = (id, data) => request.put(`/worklogs/${id}/review`, data);

/**
 * 批量审核工作日志
 * @param {Array<number>} ids - 工作日志ID列表
 * @param {Object} data - 审核信息
 * @param {string} data.reviewStatus - 审核状态
 * @param {string} data.reviewComment - 审核意见
 */
export const batchReviewWorkLog = (ids, data) => request.put('/worklogs/batch/review', data, { params: { ids } });

/**
 * 获取工作日志统计信息
 * @param {Object} params - 查询参数
 * @param {number} params.taskId - 任务ID
 * @param {number} params.userId - 用户ID
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 */
export const getWorkLogStatistics = (params) => request.get('/worklogs/statistics', { params });