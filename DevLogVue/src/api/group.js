import request from '@/utils/request'

// 获取小组列表
export function getGroupList() {
  return request({
    url: '/group/list',
    method: 'get'
  })
}

// 分页查询小组
export function getGroupPage(params) {
  return request({
    url: '/group/page',
    method: 'get',
    params: {
      current: params.current, // 页码，默认1
      size: params.size, // 每页数量，默认10
      name: params.name, // 可选，小组名称模糊搜索
      departmentId: params.departmentId, // 可选，部门ID
      leaderId: params.leaderId, // 可选，组长ID
      status: params.status // 可选，状态
    }
  })
}

// 获取小组详情
export function getGroupDetail(id) {
  return request({
    url: `/group/${id}`,
    method: 'get'
  })
}

// 创建小组
export function createGroup(data) {
  return request({
    url: '/group',
    method: 'post',
    data: {
      name: data.name,           // 必填，小组名称
      departmentId: data.departmentId,   // 必填，所属部门ID
      leaderId: data.leaderId,      // 必填，组长ID
      description: data.description,    // 可选，小组描述
      status: data.status || 1                // 可选，状态：1-启用，0-禁用，默认1
    }
  })
}

// 更新小组
export function updateGroup(id, data) {
  return request({
    url: `/group/${id}`,
    method: 'put',
    data: {
      name: data.name,           // 必填，小组名称
      departmentId: data.departmentId,   // 必填，所属部门ID
      leaderId: data.leaderId,      // 必填，组长ID
      description: data.description,    // 可选，小组描述
      status: data.status                // 可选，状态：1-启用，0-禁用
    }
  })
}

// 删除小组
export function deleteGroup(id) {
  return request({
    url: `/group/${id}`,
    method: 'delete'
  })
}

// 更新小组状态
export function updateGroupStatus(id, status) {
  return request({
    url: `/group/${id}/status`,
    method: 'put',
    data: { status }
  })
} 