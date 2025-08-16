package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.entity.po.TTaskAttachment;
import com.giao.devlogpulse.service.ITaskAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task-attachments")
public class TaskAttachmentController {

    @Autowired
    private ITaskAttachmentService taskAttachmentService;

    @PostMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "uploaderId", required = true) Long uploaderId) {
        
        // 验证文件
        if (file == null || file.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 验证上传人ID
        if (uploaderId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "上传人ID不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            TTaskAttachment attachment = taskAttachmentService.uploadAttachment(taskId, file, uploaderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.SUCCESS.getCode());
            response.put("message", ResultCode.SUCCESS.getMessage());
            response.put("data", attachment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "文件上传失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskAttachments(@PathVariable Long taskId) {
        List<TTaskAttachment> attachments = taskAttachmentService.getTaskAttachments(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResultCode.SUCCESS.getCode());
        response.put("message", ResultCode.SUCCESS.getMessage());
        response.put("data", attachments);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/{uploaderId}")
    public ResponseEntity<Map<String, Object>> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long uploaderId) {
        try {
            if (taskAttachmentService.deleteAttachment(id, uploaderId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", ResultCode.SUCCESS.getCode());
                response.put("message", ResultCode.SUCCESS.getMessage());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("code", ResultCode.FAILED.getCode());
                response.put("message", "删除附件失败：无权删除或附件不存在");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", ResultCode.FAILED.getCode());
            response.put("message", "删除附件失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        try {
            TTaskAttachment attachment = taskAttachmentService.getById(id);
            if (attachment == null || attachment.getIsDeleted() == 1) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = taskAttachmentService.downloadAttachment(id);
            String fileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 