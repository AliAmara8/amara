package com.ali.amara.auth.repository;

import com.ali.amara.auth.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {

    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryDate < :now")
    void deleteByExpiryDateBefore(Instant now);

    @Query("SELECT t FROM TokenBlacklist t WHERE t.token = :token AND t.expiryDate > :now")
    Optional<TokenBlacklist> findValidToken(String token, Instant now);
}