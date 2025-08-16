import request from '@/utils/request'

/**
 * 获取任务依赖关系
 * @param {number} taskId 任务ID
 */
export const getTaskDependencies = (taskId) => {
  return request({
    url: `/task-dependencies/${taskId}`,
    method: 'get'
  })
}

/**
 * 添加任务依赖关系
 * @param {Object} data 依赖关系数据
 */
export const addTaskDependency = (data) => {
  return request({
    url: '/task-dependencies',
    method: 'post',
    data
  })
}

/**
 * 移除任务依赖关系
 * @param {number} taskId 任务ID
 * @param {number} dependentTaskId 依赖任务ID
 */
export const removeTaskDependency = (taskId, dependentTaskId) => {
  return request({
    url: `/task-dependencies/${taskId}/${dependentTaskId}`,
    method: 'delete'
  })
}

/**
 * 检查任务依赖冲突
 * @param {number} taskId 任务ID
 * @param {number} dependentTaskId 依赖任务ID
 */
export const checkDependencyConflict = (taskId, dependentTaskId) => {
  return request({
    url: '/task-dependencies/check-conflict',
    method: 'post',
    data: { taskId, dependentTaskId }
  })
}

/**
 * 获取任务依赖路径
 * @param {number} taskId 任务ID
 */
export const getTaskDependencyPath = (taskId) => {
  return request({
    url: `/task-dependencies/${taskId}/path`,
    method: 'get'
  })
}