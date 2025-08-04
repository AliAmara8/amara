package com.ali.amara.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // La configuration par défaut de Spring est suffisante
    // @EnableScheduling active le support des tâches programmées
}
