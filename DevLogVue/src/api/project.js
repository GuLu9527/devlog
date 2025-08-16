import request from "@/utils/request";

// 创建项目
export function createProject(data) {
  return request({
    url: '/projects',
    method: 'post',
    data
  });
}

// 获取项目列表
export function getProjectList(params) {
  return request({
    url: '/projects',
    method: 'get',
    params
  });
}

// 获取项目详情
export function getProjectDetail(id) {
  return request({
    url: `/projects/${id}`,
    method: 'get'
  });
}

// 更新项目
export function updateProject(id, data) {
  return request({
    url: `/projects/${id}`,
    method: 'put',
    data
  });
}

// 删除项目
export function deleteProject(id) {
  return request({
    url: `/projects/${id}`,
    method: 'delete'
  });
}

// 获取所有项目（不分页）
export function getAllProjects() {
  return request({
    url: '/projects/all',
    method: 'get'
  });
}

// 获取项目任务统计
export function getProjectTaskStats(projectId) {
  return request({
    url: `/projects/${projectId}/stats`,
    method: 'get'
  });
}

// 获取项目进度趋势
export function getProjectProgressTrend(projectId) {
  return request({
    url: `/projects/${projectId}/progress-trend`,
    method: 'get'
  });
}