import request from "@/utils/request";

/**
 * 获取任务列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 页大小
 * @param {string} params.title - 任务标题（模糊查询）
 * @param {string} params.status - 任务状态
 * @param {string} params.priority - 优先级
 * @param {number} params.projectId - 项目ID
 * @param {number} params.creatorId - 创建人ID
 * @param {number} params.assigneeId - 负责人ID
 * @param {number} params.departmentId - 部门ID
 * @param {number} params.groupId - 组ID
 */
export const getTaskList = (params) => request.get('/tasks', { params });

/**
 * 获取所有任务列表（不分页）
 * @param {Object} params - 查询参数
 */
export const getAllTasks = (params) => request.get('/tasks/all', { params });

/**
 * 获取简化任务列表（用于选择器等）
 * @param {Object} params - 查询参数
 */
export const getSimpleTaskList = (params) => request.get('/tasks/simple', { params });

/**
 * 获取任务详情
 * @param {number} id - 任务ID
 */
export const getTaskDetail = (id) => request.get(`/tasks/${id}`);

/**
 * 创建任务
 * @param {Object} data - 任务信息
 * @param {string} data.title - 任务标题
 * @param {string} data.description - 任务描述
 * @param {string} data.priority - 优先级（高/中/低）
 * @param {number} data.projectId - 项目ID
 * @param {number} data.assigneeId - 负责人ID
 * @param {number} data.departmentId - 部门ID
 * @param {number} data.groupId - 组ID
 * @param {string} data.startTime - 开始时间
 * @param {string} data.deadline - 截止时间
 * @param {number} data.estimatedHours - 预估工时
 * @param {number} data.dependTaskId - 依赖任务ID
 */
export const createTask = (data) => request.post('/tasks', data);

/**
 * 更新任务信息
 * @param {number} id - 任务ID
 * @param {Object} data - 更新的任务信息
 */
export const updateTask = (id, data) => request.put(`/tasks/${id}`, data);

/**
 * 删除任务
 * @param {number} id - 任务ID
 */
export const deleteTask = (id) => request.delete(`/tasks/${id}`);

/**
 * 批量删除任务
 * @param {Array<number>} ids - 任务ID列表
 */
export const batchDeleteTask = (ids) => request.delete('/tasks', { data: ids });

/**
 * 更新任务状态
 * @param {number} id - 任务ID
 * @param {string} status - 任务状态
 */
export const updateTaskStatus = (id, status) => request.put(`/tasks/${id}/status`, null, { params: { status } });

/**
 * 更新任务进度
 * @param {number} id - 任务ID
 * @param {number} progress - 进度（0-100）
 */
export const updateTaskProgress = (id, progress) => request.put(`/tasks/${id}/progress`, null, { params: { progress } });

/**
 * 重新计算任务进度
 * @param {number} id - 任务ID
 */
export const recalculateTaskProgress = (id) => request.post(`/tasks/${id}/recalculate-progress`);
