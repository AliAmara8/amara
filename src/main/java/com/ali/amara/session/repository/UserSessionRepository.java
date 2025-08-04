package com.ali.amara.session.repository;

import com.ali.amara.session.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Trouve toutes les sessions actives pour un utilisateur donné.
     */
    List<UserSession> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Trouve une session spécifique par son ID et l'ID de l'utilisateur (pour la sécurité).
     */
    Optional<UserSession> findByIdAndUserId(Long sessionId, Long userId);

    // Récupère les 5 dernières sessions d'un utilisateur, triées par date de connexion décroissante
    List<UserSession> findTop5ByUserIdOrderByLoginTimeDesc(Long userId);
    Optional<UserSession> findTopByUserIdAndIsActiveTrueOrderByLoginTimeDesc(Long userId);


    /**
     * Met à jour l'heure de dernière activité pour toutes les sessions actives d'un utilisateur.
     * C'est une requête optimisée pour ne pas avoir à charger toutes les entités.
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.lastActivityTime = :now WHERE s.user.id = :userId AND s.isActive = true")
    void updateLastActivityForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserSession s SET s.lastActivityTime = :now WHERE s.id = :sessionId")
    void updateLastActivityTime(@Param("sessionId") Long sessionId, @Param("now") LocalDateTime now);

    /**
     * Trouve les sessions actives qui n'ont pas eu d'activité depuis un certain temps.
     * Utile pour une tâche planifiée qui déconnecte les sessions inactives.
     */
    List<UserSession> findByIsActiveTrueAndLastActivityTimeBefore(LocalDateTime threshold);

    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutTime = :now WHERE s.user.id = :userId AND s.isActive = true")
    void invalidateAllActiveSessionsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}