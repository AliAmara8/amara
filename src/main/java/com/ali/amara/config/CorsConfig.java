package com.ali.amara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // EXACT match!
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")); // Include OPTIONS
        configuration.setAllowedHeaders(Arrays.asList("*")); // Or specific headers
        configuration.setAllowCredentials(true); // If using credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Correct path pattern
        source.registerCorsConfiguration("/auth/**", configuration); // If needed for auth endpoints
        source.registerCorsConfiguration("/user/**", configuration);
        source.registerCorsConfiguration("/uploads/**", configuration);
        


        return source;
    }
}
