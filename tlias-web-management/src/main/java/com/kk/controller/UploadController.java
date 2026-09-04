package com.kk.controller;


import com.kk.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    @PostMapping("/upload")
    public Result upload(String name, Integer age, MultipartFile file) throws IOException {
        log.info("上传文件，姓名：{}，年龄：{}, 文件名：{}", name, age, file);
        //保存文件
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名无效");
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        String extension = dotIndex > 0 ? originalFilename.substring(dotIndex) : "";
        String newFileName = UUID.randomUUID().toString() + extension;

        File destDir = new File("D:/upload/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        file.transferTo(new File(destDir, newFileName));

        // 返回上传后的可访问 URL（相对路径，经 WebMvcConfig 静态映射对外暴露）
        return Result.success("/upload/" + newFileName);
    }
}
