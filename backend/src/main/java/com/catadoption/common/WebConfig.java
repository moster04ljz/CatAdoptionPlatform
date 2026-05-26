package com.catadoption.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：CORS、JWT 拦截器、静态资源
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${file.upload-path:E:/qClawProject/01-Java项目/CatAdoptionPlatform/uploads/}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/cat/adoption",     // 首页领养列表（公开）
                        "/api/cat/market",       // 商城列表（公开）
                        "/api/cat/hot",          // 热门猫咪（公开）
                        "/api/cat/list",         // 搜索（公开）
                        "/api/adoption/cat/*",   // 猫咪的领养申请（公开）
                        "/api/comment/cat/*",    // 评论列表（公开）
                        "/api/product/list",     // 用品列表（公开）
                        "/api/product/*",        // 用品详情（公开）
                        "/api/carousel/active",  // 轮播图（公开）
                        "/api/upload/image"      // 图片上传（公开，前端需通过表单上传）
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
