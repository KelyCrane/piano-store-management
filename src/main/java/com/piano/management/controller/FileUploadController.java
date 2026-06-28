package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload-path}")
    private String uploadPath;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".pdf", ".doc", ".docx", ".xls", ".xlsx");

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException("文件名无效");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new BusinessException("文件扩展名无效");
        }

        String suffix = originalFilename.substring(dotIndex).toLowerCase();
        if (!ALLOWED_TYPES.contains(suffix)) {
            throw new BusinessException("不支持的文件类型，仅允许上传图片和文档文件");
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

        File dir = new File(uploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建上传目录失败");
        }

        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + newFilename));
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        return Result.success("/uploads/" + newFilename);
    }
}
