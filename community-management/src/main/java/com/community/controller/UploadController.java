package com.community.controller;

import com.community.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // Check file size (5MB = 5 * 1024 * 1024)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.error("文件大小不能超过5MB");
        }

        // Check file type
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedTypes = Arrays.asList("jpg", "jpeg", "png");
        if (!allowedTypes.contains(ext)) {
            return Result.error("仅支持jpg、jpeg、png格式的图片");
        }

        // Generate unique filename
        String newName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + "." + ext;

        // Save file
        try {
            File dest = new File(uploadPath, newName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        // Build URL
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/uploads/" + newName;

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }
}