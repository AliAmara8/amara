package com.ali.amara.auth.service;

import com.ali.amara.auth.model.TokenBlacklist;
import com.ali.amara.auth.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Scheduled(fixedRate = 86400000) // Nettoyage quotidien
    public void cleanExpiredTokens() {
        tokenBlacklistRepository.deleteByExpiryDateBefore(Instant.now());
    }

    public void blacklistToken(String token) {
        Instant expiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
        tokenBlacklistRepository.save(new TokenBlacklist(token, expiryDate));
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}
