package com.ali.amara.auth.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtConfig {
    @Getter
    private String secretKey;
    private String issuer;
    private long expiration;
    private long refreshTokenExpiration;

    public String issuer() {
        return issuer;
    }

    public long expiration() {
        return expiration;
    }

    public long refreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
