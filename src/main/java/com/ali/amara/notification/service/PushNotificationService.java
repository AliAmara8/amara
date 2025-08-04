package com.ali.amara.notification.service;

import com.ali.amara.notification.entity.Notification;
import com.ali.amara.notification.exception.NotificationSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private final RestTemplate restTemplate;

    @Value("${app.firebase.server-key:}")
    private String firebaseServerKey;

    @Value("${app.firebase.fcm-url:https://fcm.googleapis.com/fcm/send}")
    private String fcmUrl;

    /**
     * Envoie une notification push
     */
    public void sendPushNotification(Notification notification) {
        try {
            if (notification.getRecipient() == null) {
                log.warn("Cannot send push notification - recipient is null for notification: {}",
                        notification.getId());
                return;
            }

            // Récupérer le token FCM de l'utilisateur (à implémenter selon votre logique)
            String fcmToken = getUserFcmToken(notification.getRecipient().getId());

            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("Cannot send push notification - FCM token is null for user: {}",
                        notification.getRecipient().getId());
                return;
            }

            Map<String, Object> pushData = buildPushData(notification, fcmToken);
            sendToFirebase(pushData);

            log.info("Push notification sent successfully for notification: {}", notification.getId());

        } catch (Exception e) {
            log.error("Failed to send push notification: {}", notification.getId(), e);
            throw new NotificationSendException("PUSH", "Failed to send push notification: " + e.getMessage(), e);
        }
    }

    /**
     * Envoie une notification push à plusieurs utilisateurs
     */
    public void sendPushNotificationToMultiple(List<String> fcmTokens, String title, String body, Map<String, String> data) {
        try {
            if (fcmTokens.isEmpty()) {
                log.warn("Cannot send push notification - no FCM tokens provided");
                return;
            }

            Map<String, Object> pushData = new HashMap<>();
            pushData.put("registration_ids", fcmTokens);
            pushData.put("notification", Map.of(
                    "title", title,
                    "body", body,
                    "icon", "ic_notification",
                    "sound", "default"
            ));

            if (data != null && !data.isEmpty()) {
                pushData.put("data", data);
            }

            sendToFirebase(pushData);

            log.info("Bulk push notification sent successfully to {} recipients", fcmTokens.size());

        } catch (Exception e) {
            log.error("Failed to send bulk push notification", e);
            throw new NotificationSendException("PUSH", "Failed to send bulk push notification: " + e.getMessage(), e);
        }
    }

    /**
     * Construit les données de la notification push
     */
    private Map<String, Object> buildPushData(Notification notification, String fcmToken) {
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("to", fcmToken);

        // Données de notification
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", generateTitle(notification));
        notificationData.put("body", notification.getMessage());
        notificationData.put("icon", "ic_notification");
        notificationData.put("sound", "default");
        notificationData.put("click_action", "OPEN_NOTIFICATION");

        pushData.put("notification", notificationData);

        // Données personnalisées
        Map<String, String> customData = new HashMap<>();
        customData.put("notificationId", notification.getId().toString());
        customData.put("type", notification.getType().name());
        customData.put("priority", notification.getPriority().name());

        if (notification.getLink() != null) {
            customData.put("link", notification.getLink());
        }

        if (notification.getEntityId() != null) {
            customData.put("entityId", notification.getEntityId().toString());
        }

        if (notification.getEntityType() != null) {
            customData.put("entityType", notification.getEntityType());
        }

        pushData.put("data", customData);

        return pushData;
    }

    /**
     * Envoie la notification à Firebase
     */
    private void sendToFirebase(Map<String, Object> pushData) {
        if (firebaseServerKey == null || firebaseServerKey.isEmpty()) {
            log.warn("Firebase server key not configured, skipping push notification");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "key=" + firebaseServerKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pushData, headers);

        try {
            restTemplate.exchange(fcmUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Failed to send push notification to Firebase", e);
            throw new NotificationSendException("PUSH", "Firebase communication failed: " + e.getMessage(), e);
        }
    }

    /**
     * Génère le titre de la notification push
     */
    private String generateTitle(Notification notification) {
        return switch (notification.getType()) {
            case RESERVATION_REQUEST -> "Nouvelle demande";
            case RESERVATION_CONFIRMED -> "Réservation confirmée";
            case RESERVATION_REJECTED -> "Réservation rejetée";
            case RESERVATION_CANCELLED -> "Réservation annulée";
            case RESERVATION_OVERDUE -> "Retard de réservation";
            case EQUIPMENT_MAINTENANCE_URGENT -> "🚨 Maintenance urgente";
            case EQUIPMENT_MAINTENANCE_HIGH -> "⚠️ Maintenance prioritaire";
            case EQUIPMENT_MAINTENANCE -> "Maintenance requise";
            default -> "Notification";
        };
    }

    /**
     * Récupère le token FCM de l'utilisateur
     * À implémenter selon votre logique de stockage des tokens
     */
    private String getUserFcmToken(Long userId) {
        // TODO: Implémenter la récupération du token FCM
        // Exemple: return userDeviceRepository.findActiveTokenByUserId(userId);
        log.warn("FCM token retrieval not implemented for user: {}", userId);
        return null;
    }
}