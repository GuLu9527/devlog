package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TTaskAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITaskAttachmentService extends IService<TTaskAttachment> {
    
    /**
     * 上传任务附件
     *
     * @param taskId 任务ID
     * @param file 文件
     * @param uploaderId 上传人ID
     * @return 附件信息
     */
    TTaskAttachment uploadAttachment(Long taskId, MultipartFile file, Long uploaderId);

    /**
     * 获取任务的所有附件
     *
     * @param taskId 任务ID
     * @return 附件列表
     */
    List<TTaskAttachment> getTaskAttachments(Long taskId);

    /**
     * 删除任务附件
     *
     * @param attachmentId 附件ID
     * @param uploaderId 上传人ID
     * @return 是否删除成功
     */
    boolean deleteAttachment(Long attachmentId, Long uploaderId);

    /**
     * 下载任务附件
     *
     * @param attachmentId 附件ID
     * @return 附件文件字节数组和文件名
     */
    byte[] downloadAttachment(Long attachmentId);
} 