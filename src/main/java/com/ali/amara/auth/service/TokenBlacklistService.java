package com.ali.amara.auth.service;

import com.ali.amara.auth.model.TokenBlacklist;
import com.ali.amara.auth.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtService jwtService;

    /**
     * Ajoute un token à la blacklist
     */
    @Transactional
    public void blacklistToken(String token) {
        try {
            // Calculer la date d'expiration basée sur le token JWT lui-même
            Instant expiryDate = jwtService.getExpirationInstantFromToken(token);

            // Ajouter une marge de sécurité de 1 heure
            expiryDate = expiryDate.plus(1, ChronoUnit.HOURS);

            TokenBlacklist blacklistedToken = TokenBlacklist.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .createdAt(Instant.now())
                    .build();

            tokenBlacklistRepository.save(blacklistedToken);

            log.info("Token blacklisted successfully, expires at: {}", expiryDate);

        } catch (Exception e) {
            log.error("Failed to blacklist token", e);
            // En cas d'échec, on ajoute quand même avec une expiration par défaut
            Instant defaultExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
            TokenBlacklist fallbackToken = new TokenBlacklist(token, defaultExpiry);
            tokenBlacklistRepository.save(fallbackToken);
            log.warn("Token blacklisted with default expiration due to error");
        }
    }

    /**
     * Vérifie si un token est dans la blacklist (avec cache)
     */
    @Cacheable(value = "tokenBlacklist", key = "#token", condition = "#token != null")
    public boolean isBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            boolean isBlacklisted = tokenBlacklistRepository.existsByTokenAndNotExpired(token, Instant.now());

            if (isBlacklisted) {
                log.debug("Token found in blacklist: {}", maskToken(token));
            }

            return isBlacklisted;

        } catch (Exception e) {
            log.error("Error checking token blacklist status", e);
            // En cas d'erreur, on considère le token comme non blacklisté pour éviter de bloquer l'utilisateur
            return false;
        }
    }

    /**
     * Nettoyage quotidien des tokens expirés
     */
    @Scheduled(fixedRate = 86400000) // 24 heures
    @Transactional
    public void cleanExpiredTokens() {
        try {
            long startTime = System.currentTimeMillis();
            int deletedCount = tokenBlacklistRepository.deleteByExpiryDateBefore(Instant.now());
            long duration = System.currentTimeMillis() - startTime;

            if (deletedCount > 0) {
                log.info("Cleaned {} expired tokens from blacklist in {}ms", deletedCount, duration);
            } else {
                log.debug("No expired tokens to clean");
            }

        } catch (Exception e) {
            log.error("Error during token blacklist cleanup", e);
        }
    }

    /**
     * Nettoyage profond hebdomadaire
     */
    @Scheduled(cron = "0 2 * * SUN") // Dimanche à 2h du matin
    @Transactional
    public void deepCleanExpiredTokens() {
        try {
            // Supprimer les tokens expirés depuis plus de 7 jours
            Instant cleanupDate = Instant.now().minus(7, ChronoUnit.DAYS);
            int deletedCount = tokenBlacklistRepository.deleteExpiredTokensOlderThan(cleanupDate);

            log.info("Deep cleanup: removed {} old expired tokens", deletedCount);

        } catch (Exception e) {
            log.error("Error during deep token cleanup", e);
        }
    }

    /**
     * Monitoring quotidien des statistiques
     */
    @Scheduled(cron = "0 1 * * *") // Tous les jours à 1h du matin
    public void logBlacklistStatistics() {
        try {
            long activeTokensCount = tokenBlacklistRepository.countActiveBlacklistedTokens(Instant.now());
            log.info("Blacklist statistics: {} active blacklisted tokens", activeTokensCount);

            if (activeTokensCount > 10000) {
                log.warn("High number of blacklisted tokens detected: {}", activeTokensCount);
            }

        } catch (Exception e) {
            log.error("Error retrieving blacklist statistics", e);
        }
    }

    /**
     * Révoque tous les tokens d'un utilisateur (en cas de compromission)
     */
    @Transactional
    public void revokeAllUserTokens(String userEmail) {
        log.warn("Revoking all tokens for user: {}", userEmail);
        // Cette méthode nécessiterait d'étendre le modèle pour tracker les tokens par utilisateur
        // Pour l'instant, on log l'action pour audit
    }

    /**
     * Masque le token pour les logs (sécurité)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

    /**
     * Méthode utilitaire pour les tests et le monitoring
     */
    public long getActiveBlacklistedTokensCount() {
        return tokenBlacklistRepository.countActiveBlacklistedTokens(Instant.now());
    }

    /**
     * Force le nettoyage (pour maintenance manuelle)
     */
    @Transactional
    public int forceCleanup() {
        return tokenBlacklistRepository.deleteByExpiryDateBefore(Instant.now());
    }
}