package com.ali.amara.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.ali.amara.relationship",
    "com.ali.amara.recommendation",
    "com.ali.amara.profile"
})
public class ComponentScanConfig {
    // Configuration de scan des composants
}
