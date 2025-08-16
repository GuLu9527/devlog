import request from "@/utils/request";

// 获取日志模板列表
export function getLogTemplateList(params) {
  return request({
    url: '/log-templates',
    method: 'get',
    params
  });
}

// 获取日志模板详情
export function getLogTemplateDetail(id) {
  return request({
    url: `/log-templates/${id}`,
    method: 'get'
  });
}

// 更新日志模板
export function updateLogTemplate(id, data) {
  return request({
    url: `/log-templates/${id}`,
    method: 'put',
    data
  });
} 