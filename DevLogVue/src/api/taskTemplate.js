import request from '@/utils/request'

/**
 * 获取任务模板列表
 * @param {Object} params 查询参数
 */
export const getTaskTemplateList = (params) => {
  return request({
    url: '/task-templates',
    method: 'get',
    params
  })
}

/**
 * 获取模板详情
 * @param {number} id 模板ID
 */
export const getTaskTemplateById = (id) => {
  return request({
    url: `/task-templates/${id}`,
    method: 'get'
  })
}

/**
 * 创建任务模板
 * @param {Object} data 模板数据
 */
export const createTaskTemplate = (data) => {
  return request({
    url: '/task-templates',
    method: 'post',
    data
  })
}

/**
 * 更新任务模板
 * @param {number} id 模板ID
 * @param {Object} data 模板数据
 */
export const updateTaskTemplate = (id, data) => {
  return request({
    url: `/task-templates/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除任务模板
 * @param {number} id 模板ID
 */
export const deleteTaskTemplate = (id) => {
  return request({
    url: `/task-templates/${id}`,
    method: 'delete'
  })
}

/**
 * 应用任务模板
 * @param {number} id 模板ID
 * @param {Object} data 应用配置
 */
export const applyTaskTemplate = (id, data) => {
  return request({
    url: `/task-templates/${id}/apply`,
    method: 'post',
    data
  })
}

/**
 * 克隆任务模板
 * @param {number} id 模板ID
 * @param {string} newName 新名称
 */
export const cloneTaskTemplate = (id, newName) => {
  return request({
    url: `/task-templates/${id}/clone`,
    method: 'post',
    data: { newName }
  })
}

/**
 * 按分类获取模板
 * @param {string} category 分类
 */
export const getTaskTemplatesByCategory = (category) => {
  return request({
    url: `/task-templates/category/${category}`,
    method: 'get'
  })
}

/**
 * 获取热门模板
 * @param {number} limit 数量限制
 */
export const getPopularTaskTemplates = (limit = 10) => {
  return request({
    url: '/task-templates/popular',
    method: 'get',
    params: { limit }
  })
}

/**
 * 导入模板
 * @param {Object} data 模板数据
 */
export const importTaskTemplate = (data) => {
  return request({
    url: '/task-templates/import',
    method: 'post',
    data
  })
}