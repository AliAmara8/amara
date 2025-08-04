package com.ali.amara.notification.service;

import com.ali.amara.notification.NotificationType;
import com.ali.amara.notification.dto.NotificationPreferencesDTO;
import com.ali.amara.notification.entity.NotificationPreferences;
import com.ali.amara.notification.repository.NotificationPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationPreferenceService {

    private final NotificationPreferencesRepository preferencesRepository;

    public boolean shouldReceiveNotification(Long userId, NotificationType type) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            return true; // Par défaut, accepter toutes les notifications
        }

        NotificationPreferences pref = preferences.get();

        // Vérifier si les notifications sont globalement activées
        if (!pref.isInAppEnabled()) {
            return false;
        }

        // Vérifier les heures de silence
        if (pref.isQuietHours() && isInQuietHours(pref)) {
            return false;
        }

        // Vérifier les préférences par catégorie
        Boolean categoryEnabled = pref.getCategoryPreferences().get(type.getCategory());
        if (categoryEnabled != null && !categoryEnabled) {
            return false;
        }

        // Vérifier les préférences par type
        Boolean typeEnabled = pref.getTypePreferences().get(type);
        if (typeEnabled != null && !typeEnabled) {
            return false;
        }

        return true;
    }

    public boolean shouldSendEmail(Long userId, NotificationType type) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            return false; // Par défaut, ne pas envoyer d'emails
        }

        NotificationPreferences pref = preferences.get();
        return pref.isEmailEnabled() && shouldReceiveNotification(userId, type);
    }

    public boolean shouldSendPush(Long userId, NotificationType type) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            return true; // Par défaut, envoyer les push
        }

        NotificationPreferences pref = preferences.get();
        return pref.isPushEnabled() && shouldReceiveNotification(userId, type);
    }

    private boolean isInQuietHours(NotificationPreferences pref) {
        if (pref.getQuietHoursStart() == null || pref.getQuietHoursEnd() == null) {
            return false;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = pref.getQuietHoursStart().toLocalTime();
        LocalTime end = pref.getQuietHoursEnd().toLocalTime();

        if (start.isBefore(end)) {
            return now.isAfter(start) && now.isBefore(end);
        } else {
            return now.isAfter(start) || now.isBefore(end);
        }
    }

    public NotificationPreferencesDTO getPreferences(Long userId) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            return createDefaultPreferences(userId);
        }

        return mapToDTO(preferences.get());
    }

    public NotificationPreferencesDTO updatePreferences(Long userId, NotificationPreferencesDTO dto) {
        NotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElse(new NotificationPreferences());

        preferences.setUserId(userId);
        preferences.setEmailEnabled(dto.isEmailEnabled());
        preferences.setPushEnabled(dto.isPushEnabled());
        preferences.setInAppEnabled(dto.isInAppEnabled());
        preferences.setCategoryPreferences(dto.getCategoryPreferences());
        preferences.setTypePreferences(dto.getTypePreferences());
        preferences.setEmailFrequency(dto.getEmailFrequency());
        preferences.setQuietHours(dto.isQuietHours());
        preferences.setQuietHoursStart(dto.getQuietHoursStart());
        preferences.setQuietHoursEnd(dto.getQuietHoursEnd());

        preferences = preferencesRepository.save(preferences);
        return mapToDTO(preferences);
    }

    private NotificationPreferencesDTO createDefaultPreferences(Long userId) {
        NotificationPreferencesDTO dto = new NotificationPreferencesDTO();
        dto.setUserId(userId);
        dto.setEmailEnabled(false);
        dto.setPushEnabled(true);
        dto.setInAppEnabled(true);
        dto.setEmailFrequency("DAILY");
        dto.setQuietHours(false);
        return dto;
    }

    private NotificationPreferencesDTO mapToDTO(NotificationPreferences pref) {
        NotificationPreferencesDTO dto = new NotificationPreferencesDTO();
        dto.setUserId(pref.getUserId());
        dto.setEmailEnabled(pref.isEmailEnabled());
        dto.setPushEnabled(pref.isPushEnabled());
        dto.setInAppEnabled(pref.isInAppEnabled());
        dto.setCategoryPreferences(pref.getCategoryPreferences());
        dto.setTypePreferences(pref.getTypePreferences());
        dto.setEmailFrequency(pref.getEmailFrequency());
        dto.setQuietHours(pref.isQuietHours());
        dto.setQuietHoursStart(pref.getQuietHoursStart());
        dto.setQuietHoursEnd(pref.getQuietHoursEnd());
        return dto;
    }
}
