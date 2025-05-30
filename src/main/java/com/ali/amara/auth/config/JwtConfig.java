package com.ali.amara.auth.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String secret,
        Long expiration,
        String issuer
) {}
