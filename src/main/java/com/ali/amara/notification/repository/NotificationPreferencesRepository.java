package com.ali.amara.notification.repository;

import com.ali.amara.notification.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    /**
     * Trouve les préférences par ID utilisateur
     */
    Optional<NotificationPreferences> findByUserId(Long userId);

    /**
     * Vérifie si un utilisateur a des préférences définies
     */
    boolean existsByUserId(Long userId);

    /**
     * Supprime les préférences d'un utilisateur
     */
    void deleteByUserId(Long userId);

    /**
     * Trouve tous les utilisateurs avec notifications email activées
     */
    @Query("SELECT np FROM NotificationPreferences np WHERE np.emailEnabled = true")
    List<NotificationPreferences> findAllWithEmailEnabled();

    /**
     * Trouve tous les utilisateurs avec notifications push activées
     */
    @Query("SELECT np FROM NotificationPreferences np WHERE np.pushEnabled = true")
    List<NotificationPreferences> findAllWithPushEnabled();

    /**
     * Trouve les utilisateurs avec une fréquence d'email spécifique
     */
    @Query("SELECT np FROM NotificationPreferences np WHERE np.emailFrequency = :frequency AND np.emailEnabled = true")
    List<NotificationPreferences> findByEmailFrequency(@Param("frequency") String frequency);

    /**
     * Compte le nombre d'utilisateurs avec des préférences configurées
     */
    @Query("SELECT COUNT(np) FROM NotificationPreferences np")
    long countConfiguredUsers();
}