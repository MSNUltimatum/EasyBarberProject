package com.dreamteam.easybarber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private String uploadPath = System.getProperty("user.dir") + "/upload";

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:/" + uploadPath + File.separator);
    }

    @Bean
    public Path sourcesPath() {
        return Paths.get(System.getProperty("catalina.home")+ File.separator + "webapps"+
                File.separator + "expanded" /*"ROOT"*/+
                File.separator + "myresources");
    }
}