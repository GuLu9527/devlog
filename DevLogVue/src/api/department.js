import request from '@/utils/request'

/**
 * 获取部门列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 页大小
 * @param {string} params.name - 部门名称（模糊查询）
 * @param {number} params.status - 状态（1启用 0禁用）
 */
export const getDepartmentList = (params) => request.get('/departments', { params })

/**
 * 获取部门树形结构
 */
export const getDepartmentTree = () => request.get('/departments/tree')

/**
 * 获取部门详情
 * @param {number} id - 部门ID
 */
export const getDepartmentDetail = (id) => request.get(`/departments/${id}`)

/**
 * 创建部门
 * @param {Object} data - 部门信息
 * @param {string} data.name - 部门名称
 * @param {number} data.parentId - 父部门ID
 * @param {number} data.status - 状态（1启用 0禁用）
 */
export const createDepartment = (data) => request.post('/departments', data)

/**
 * 更新部门信息
 * @param {number} id - 部门ID
 * @param {Object} data - 更新的部门信息
 */
export const updateDepartment = (id, data) => request.put(`/departments/${id}`, data)

/**
 * 删除部门
 * @param {number} id - 部门ID
 */
export const deleteDepartment = (id) => request.delete(`/departments/${id}`)

/**
 * 获取根部门列表
 */
export const getDepartmentRootList = () => request.get('/departments/root')
