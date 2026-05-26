package com.catadoption.controller;

import com.catadoption.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "E:/qClawProject/01-Java项目/CatAdoptionPlatform/uploads/";

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());

            String url = "/uploads/" + fileName;
            return Result.success("上传成功", url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
