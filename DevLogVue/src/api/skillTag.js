import request from '@/utils/request'

/**
 * 技能标签API
 */

// 分页查询标签列表
export function getSkillTagList(params) {
  return request({
    url: '/skill-tags',
    method: 'get',
    params
  })
}

// 根据分类查询标签
export function getSkillTagsByCategory(category) {
  return request({
    url: `/skill-tags/category/${category}`,
    method: 'get'
  })
}

// 查询所有启用的标签
export function getAllEnabledSkillTags() {
  return request({
    url: '/skill-tags/enabled',
    method: 'get'
  })
}

// 获取标签详情
export function getSkillTagById(id) {
  return request({
    url: `/skill-tags/${id}`,
    method: 'get'
  })
}

// 创建标签
export function createSkillTag(data) {
  return request({
    url: '/skill-tags',
    method: 'post',
    data
  })
}

// 更新标签
export function updateSkillTag(id, data) {
  return request({
    url: `/skill-tags/${id}`,
    method: 'put',
    data
  })
}

// 删除标签
export function deleteSkillTag(id) {
  return request({
    url: `/skill-tags/${id}`,
    method: 'delete'
  })
}

// 批量删除标签
export function batchDeleteSkillTags(ids) {
  return request({
    url: '/skill-tags/batch',
    method: 'delete',
    data: ids
  })
}

// 批量导入标签
export function batchImportSkillTags(data) {
  return request({
    url: '/skill-tags/batch-import',
    method: 'post',
    data
  })
}

// 获取标签使用统计
export function getSkillTagStatistics() {
  return request({
    url: '/skill-tags/statistics',
    method: 'get'
  })
}

// 检查标签名称是否存在
export function checkSkillTagNameExists(tagName, excludeId = null) {
  return request({
    url: '/skill-tags/check-name',
    method: 'get',
    params: { tagName, excludeId }
  })
}

// 获取所有分类
export function getSkillTagCategories() {
  return request({
    url: '/skill-tags/categories',
    method: 'get'
  })
}