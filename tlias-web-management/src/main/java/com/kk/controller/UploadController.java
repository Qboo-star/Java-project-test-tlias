package com.kk.controller;


import com.kk.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    /** 允许上传的图片扩展名白名单 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    /** 上传文件保存目录，由 application.yml 中 tlias.upload.dir 配置 */
    @Value("${tlias.upload.dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        // 基本校验
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名无效");
        }

        // 扩展名校验，防止上传可执行文件 / 脚本文件
        int dotIndex = originalFilename.lastIndexOf(".");
        String extension = dotIndex >= 0 ? originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT) : "";
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("非法文件类型上传被拦截：{}", originalFilename);
            return Result.error("仅支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        //保存文件（文件名使用 UUID，避免覆盖与路径猜测）
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        File destDir = new File(uploadDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        file.transferTo(new File(destDir, newFileName));
        log.info("文件上传成功：{} -> {}", originalFilename, newFileName);

        // 返回上传后的可访问 URL（相对路径，经 WebMvcConfig 静态映射对外暴露）
        return Result.success("/upload/" + newFileName);
    }
}
