import request from "@/utils/request";

// 获取操作日志列表
export function getOperationLogList(params) {
  return request({
    url: '/operation-logs',
    method: 'get',
    params
  });
}

// 获取操作日志详情
export function getOperationLogDetail(id) {
  return request({
    url: `/operation-logs/${id}`,
    method: 'get'
  });
}

// 获取操作日志统计
export function getOperationLogStatistics(params) {
  return request({
    url: '/operation-logs/statistics',
    method: 'get',
    params
  });
} 