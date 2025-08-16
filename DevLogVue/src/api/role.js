import request from "@/utils/request";

//查询角色列表
export const queryPageApi = (pageNum,pageSize,name) =>request.get(`/roles?pageNum=${pageNum}&pageSize=${pageSize}&name=${name}`);

//新增角色
export const addRoleApi = (data) =>request.post(`/roles`,data);

//修改角色
export const updateRoleApi = (data) =>request.put(`/roles`,data);

//删除角色
export const deleteRoleApi = (id) =>request.delete(`/roles/${id}`);

//查询角色详情
export const getRoleByIdApi = (id) =>request.get(`/roles/${id}`);

//查询所有角色，用于其他页面
export const findAllApi = () =>request.get(`/roles/all`);

//查询属于此角色名的用户
export const getUserByRoleNameApi = (roleName) =>request.get(`/roles/users/${roleName}`);
