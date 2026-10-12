package com.hostelfinder.config;

import com.hostelfinder.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serves uploaded images at /uploads/** */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final FileStorageService files;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(files.root().toUri().toString())
                .setCachePeriod(3600);
    }
}
