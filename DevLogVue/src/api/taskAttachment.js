import request from '@/utils/request';

// 上传任务附件
export function uploadTaskAttachmentApi(taskId, formData) {
  return request({
    url: `/task-attachments/${taskId}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 获取任务附件列表
export function getTaskAttachmentsApi(taskId) {
  return request({
    url: `/task-attachments/task/${taskId}`,
    method: 'get'
  });
}

// 删除任务附件
export function deleteTaskAttachmentApi(id,uploaderId) {
  return request({
    url: `/task-attachments/${id}/${uploaderId}`,
    method: 'delete'
  });
}

// 下载任务附件
export function downloadTaskAttachmentApi(id) {
  return request({
    url: `/task-attachments/download/${id}`,
    method: 'get',
    responseType: 'blob'
  });
}

// 预览任务附件
export function previewTaskAttachmentApi(id) {
  return request({
    url: `/task-attachments/${id}/preview`,
    method: 'get'
  });
} 