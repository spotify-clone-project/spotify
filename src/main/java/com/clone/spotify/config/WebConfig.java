package com.clone.spotify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기존의 정적 리소스 위치를 설정
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 새로운 리소스 경로 매핑 추가
        registry.addResourceHandler("/file/music/**")
                .addResourceLocations("file:/Users/UK/Desktop/MusicList/songs/", "file:/Users/UK/Desktop/MusicList/images/", "file:/Users/UK/Desktop/MusicList/lyrics/");
    }
}

