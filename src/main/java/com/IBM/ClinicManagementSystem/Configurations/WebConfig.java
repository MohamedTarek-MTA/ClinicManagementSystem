package com.IBM.ClinicManagementSystem.Configurations;

import com.IBM.ClinicManagementSystem.Utils.Factory.StringToEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
    private final StringToEnumConverterFactory stringToEnumConverterFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToEnumConverterFactory);
    }
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;
    @Override
    public  void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins) // React dev server
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}