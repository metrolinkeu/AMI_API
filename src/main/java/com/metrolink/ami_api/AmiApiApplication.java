package com.metrolink.ami_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@SpringBootApplication
public class AmiApiApplication {

    public static void main(String[] args) {

        System.out.println("cambio de prueba");

        // Determinar el sistema operativo y crear la carpeta en la ubicación adecuada
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            createFolderIfNotExists("C:\\ApiAmiMetrolink");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            createFolderIfNotExists("/ApiAmiMetrolink");
        }

        SpringApplication.run(AmiApiApplication.class, args);
    }

    private static void createFolderIfNotExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Carpeta creada: " + folderPath);
            } else {
                System.out.println("No se pudo crear la carpeta: " + folderPath);
            }
        } else {
            System.out.println("La carpeta ya existe: " + folderPath);
        }
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
