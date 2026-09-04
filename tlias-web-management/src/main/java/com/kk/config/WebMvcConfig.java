package com.kk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射：将 /upload/** 的访问映射到本地磁盘 D:/upload/
 * 这样上传后的图片可通过 http://localhost:8080/upload/xxx.jpg 直接访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:D:/upload/");
    }
}
