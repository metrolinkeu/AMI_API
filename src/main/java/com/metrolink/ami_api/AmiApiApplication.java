package com.metrolink.ami_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AmiApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmiApiApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // registry.addMapping("/**")
                // .allowedOrigins("http://localhost:8086") // Permite solo este origen
                // //.allowedOrigins("*") // Permite todos los origenes
                // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                // .allowedHeaders("*")
                // .allowCredentials(true);

                registry.addMapping("/**")
                        .allowedOrigins("*") // Permitir todos los orígenes
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                        .allowedHeaders("*");
                        //.allowCredentials(true);
            }
        };
    }

}
