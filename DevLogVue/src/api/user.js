import request from "@/utils/request";

/**
 * 获取用户列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 页大小
 * @param {string} params.username - 用户名（模糊查询）
 * @param {string} params.realName - 真实姓名（模糊查询）
 * @param {string} params.email - 邮箱（模糊查询）
 * @param {string} params.phone - 手机号（模糊查询）
 * @param {number} params.status - 状态（1启用 0禁用）
 * @param {number} params.roleId - 角色ID
 * @param {number} params.departmentId - 部门ID
 * @param {number} params.groupId - 组ID
 */
export const getUserList = (params) => request.get('/users', { params });

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 */
export const getUserDetail = (id) => request.get(`/users/${id}`);

/**
 * 创建用户
 * @param {Object} data - 用户信息
 */
export const createUser = (data) => request.post('/users', data);

/**
 * 更新用户信息
 * @param {number} id - 用户ID
 * @param {Object} data - 更新的用户信息
 */
export const updateUser = (id, data) => request.put(`/users/${id}`, data);

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export const deleteUser = (id) => request.delete(`/users/${id}`);

/**
 * 批量删除用户
 * @param {Array<number>} ids - 用户ID列表
 */
export const batchDeleteUser = (ids) => request.delete('/users', { data: ids });

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @param {string} data.confirmPassword - 确认密码
 */
export const changePassword = (data) => request.put('/users/password', data);

/**
 * 重置用户密码（管理员功能）
 * @param {number} id - 用户ID
 * @param {Object} data - 密码信息
 * @param {string} data.newPassword - 新密码
 */
export const resetPassword = (id, data) => request.post(`/users/${id}/password/reset`, data); 