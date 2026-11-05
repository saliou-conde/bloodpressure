package ch.bloodpressure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private static final String[] URLS = {
            "http://localhost:4200", // Angular Dev
            "http://localhost:3000", // Angular Dev
            "https://bloodpressure.ch", // Productive
            "https://www.bloodpressure.ch", // Productive
            "http://139.162.135.188:3000" // Angular Linode Server
    };

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(URLS) // your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
