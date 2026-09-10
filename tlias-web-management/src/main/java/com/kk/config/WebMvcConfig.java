package com.kk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 静态资源映射：将 /upload/** 的访问映射到配置的本地上传目录（tlias.upload.dir）
 * 这样上传后的图片可通过 http://localhost:8080/upload/xxx.jpg 直接访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${tlias.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保目录路径以分隔符结尾，符合 file: 资源位置约定
        String location = uploadDir.endsWith(File.separator) || uploadDir.endsWith("/")
                ? uploadDir
                : uploadDir + File.separator;
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + location);
    }
}
