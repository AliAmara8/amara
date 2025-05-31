package com.ali.amara.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token_blacklist", indexes = {
        @Index(name = "idx_token_blacklist_expiry", columnList = "expiryDate"),
        @Index(name = "idx_token_blacklist_token", columnList = "token")
})
public class TokenBlacklist {

    @Id
    @Column(length = 512, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Constructeur personnalisé pour le service
    public TokenBlacklist(String token, Instant expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.createdAt = Instant.now();
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}