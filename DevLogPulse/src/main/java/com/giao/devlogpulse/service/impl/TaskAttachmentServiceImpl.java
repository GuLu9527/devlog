package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TTaskAttachment;
import com.giao.devlogpulse.mapper.TTaskAttachmentMapper;
import com.giao.devlogpulse.service.ITaskAttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class TaskAttachmentServiceImpl extends ServiceImpl<TTaskAttachmentMapper, TTaskAttachment> implements ITaskAttachmentService {

    @Value("${file.upload.path}")
    private String uploadPath;

    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    // 允许的文件类型
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain", "application/zip", "application/x-rar-compressed"
    );

    @Override
    public TTaskAttachment uploadAttachment(Long taskId, MultipartFile file, Long uploaderId) {
        try {
            // 生成文件路径
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("文件名不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new IllegalArgumentException("无法识别文件类型");
            }

            // 检查是否为图片
            boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
            // 检查是否为允许的文件类型
            boolean isAllowedFile = ALLOWED_FILE_TYPES.contains(contentType);

            if (!isImage && !isAllowedFile) {
                throw new IllegalArgumentException("不支持的文件类型：" + contentType);
            }

            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileType;
            // 根据文件类型选择存储目录
            String subDir = isImage ? "images" : "files";
            String relativePath = "task/" + taskId + "/" + subDir + "/" + fileName;
            String fullPath = uploadPath + "/" + relativePath;

            // 确保目录存在
            Path directory = Paths.get(uploadPath, "task", taskId.toString(), subDir);
            Files.createDirectories(directory);

            // 保存文件
            File dest = new File(fullPath);
            file.transferTo(dest);

            // 保存附件信息
            TTaskAttachment attachment = new TTaskAttachment();
            attachment.setTaskId(taskId);
            attachment.setFileName(originalFilename);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(fileType);
            attachment.setOssPath(relativePath);
            attachment.setUploaderId(uploaderId);
            attachment.setIsDeleted(0);
            attachment.setCreateTime(LocalDateTime.now());
            attachment.setUpdateTime(LocalDateTime.now());

            save(attachment);
            return attachment;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public List<TTaskAttachment> getTaskAttachments(Long taskId) {
        LambdaQueryWrapper<TTaskAttachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskAttachment::getTaskId, taskId)
                   .eq(TTaskAttachment::getIsDeleted, 0)
                   .orderByDesc(TTaskAttachment::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public boolean deleteAttachment(Long attachmentId, Long uploaderId) {
        TTaskAttachment attachment = getById(attachmentId);
        if (attachment == null || !attachment.getUploaderId().equals(uploaderId)) {
            return false;
        }

        // 删除本地文件
        String fullPath = uploadPath + "/" + attachment.getOssPath();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }

        // 更新数据库记录
        attachment.setIsDeleted(1);
        attachment.setUpdateTime(LocalDateTime.now());
        return updateById(attachment);
    }

    @Override
    public byte[] downloadAttachment(Long attachmentId) {
        TTaskAttachment attachment = getById(attachmentId);
        if (attachment == null || attachment.getIsDeleted() == 1) {
            throw new RuntimeException("附件不存在或已被删除");
        }

        try {
            String fullPath = uploadPath + "/" + attachment.getOssPath();
            Path filePath = Paths.get(fullPath);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("文件不存在");
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
} 