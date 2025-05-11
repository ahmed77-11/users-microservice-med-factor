package com.medfactor.factorusers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig  implements WebMvcConfigurer{



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the /uploads/** URL to the actual file system location
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/mghir/Documents/PFE/factor-users/src/main/resources/static/uploads/");
    }



}
