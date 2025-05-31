package com.ali.amara.auth.repository;

import com.ali.amara.auth.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {

    /**
     * Vérifie si un token existe dans la blacklist et n'est pas expiré
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM TokenBlacklist t WHERE t.token = :token AND t.expiryDate > :now")
    boolean existsByTokenAndNotExpired(@Param("token") String token, @Param("now") Instant now);

    /**
     * Vérifie simplement l'existence du token (pour compatibilité)
     */
    boolean existsByToken(String token);

    /**
     * Supprime tous les tokens expirés
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryDate < :now")
    int deleteByExpiryDateBefore(@Param("now") Instant now);

    /**
     * Trouve un token valide (non expiré)
     */
    @Query("SELECT t FROM TokenBlacklist t WHERE t.token = :token AND t.expiryDate > :now")
    Optional<TokenBlacklist> findValidToken(@Param("token") String token, @Param("now") Instant now);

    /**
     * Trouve tous les tokens expirés (pour le nettoyage par batch)
     */
    @Query("SELECT t FROM TokenBlacklist t WHERE t.expiryDate < :now")
    List<TokenBlacklist> findExpiredTokens(@Param("now") Instant now);

    /**
     * Compte le nombre de tokens dans la blacklist
     */
    @Query("SELECT COUNT(t) FROM TokenBlacklist t WHERE t.expiryDate > :now")
    long countActiveBlacklistedTokens(@Param("now") Instant now);

    /**
     * Supprime tous les tokens expirés depuis plus de X jours (nettoyage profond)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryDate < :cleanupDate")
    int deleteExpiredTokensOlderThan(@Param("cleanupDate") Instant cleanupDate);

    /**
     * Trouve les tokens proches de l'expiration (pour notification/monitoring)
     */
    @Query("SELECT t FROM TokenBlacklist t WHERE t.expiryDate BETWEEN :now AND :soonExpiry")
    List<TokenBlacklist> findTokensExpiringSoon(@Param("now") Instant now, @Param("soonExpiry") Instant soonExpiry);
}