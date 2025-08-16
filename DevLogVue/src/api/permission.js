import request from "@/utils/request";

// 创建权限
export function createPermission(data) {
  return request({
    url: '/permissions',
    method: 'post',
    data
  });
}

// 获取权限列表
export function getPermissionList(params) {
  return request({
    url: '/permissions',
    method: 'get',
    params
  });
}

// 获取权限详情
export function getPermissionDetail(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'get'
  });
}

// 分配角色权限
export function assignRolePermissions(roleId, permissionIds) {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: 'post',
    data: permissionIds
  });
}

// 获取角色权限列表
export function getRolePermissions(id) {
  return request({
    url: `/roles/${id}/permissions`,
    method: 'get'
  });
}

// 获取角色权限ID列表
export function getRolePermissionIds(id) {
  return request({
    url: `/roles/${id}/permission-ids`,
    method: 'get'
  });
}

// 获取所有权限列表（不分页）
export function getAllPermissions() {
  return request({
    url: '/permissions',
    method: 'get',
    params: {
      pageNum: 1,
      pageSize: 1000 // 获取所有权限
    }
  });
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/permissions/${id}`,
    method: 'put',
    data
  });
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  });
} 