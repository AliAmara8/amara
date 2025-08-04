package com.ali.amara.notification.service;

import com.ali.amara.notification.NotificationType;
import com.ali.amara.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationTemplateService {

    public String generateMessage(NotificationType type, Map<String, String> metadata, User actor) {
        return switch (type) {
            case RESERVATION_REQUEST -> generateReservationRequestMessage(metadata, actor);
            case RESERVATION_CONFIRMED -> generateReservationConfirmedMessage(metadata, actor);
            case RESERVATION_REJECTED -> generateReservationRejectedMessage(metadata, actor);
            case RESERVATION_CANCELLED -> generateReservationCancelledMessage(metadata, actor);
            case RESERVATION_STARTED -> generateReservationStartedMessage(metadata);
            case RESERVATION_ENDING_SOON -> generateReservationEndingSoonMessage(metadata);
            case RESERVATION_OVERDUE -> generateReservationOverdueMessage(metadata);
            case EQUIPMENT_MAINTENANCE -> generateMaintenanceMessage(metadata);
            case EQUIPMENT_MAINTENANCE_HIGH -> generateMaintenanceHighMessage(metadata);
            case EQUIPMENT_MAINTENANCE_URGENT -> generateMaintenanceUrgentMessage(metadata);
            default -> type.getDefaultMessage();
        };
    }

    private String generateReservationRequestMessage(Map<String, String> metadata, User actor) {
        String equipmentName = metadata.get("equipmentName");
        String userName = actor != null ? actor.getFullName() : "Un utilisateur";
        String startDate = metadata.get("startDate");
        String endDate = metadata.get("endDate");

        return String.format("%s souhaite réserver %s du %s au %s",
                userName, equipmentName, formatDate(startDate), formatDate(endDate));
    }

    private String generateReservationConfirmedMessage(Map<String, String> metadata, User actor) {
        String equipmentName = metadata.get("equipmentName");
        String startDate = metadata.get("startDate");
        String endDate = metadata.get("endDate");

        return String.format("Votre réservation de %s du %s au %s a été confirmée",
                equipmentName, formatDate(startDate), formatDate(endDate));
    }

    private String generateReservationRejectedMessage(Map<String, String> metadata, User actor) {
        String equipmentName = metadata.get("equipmentName");
        String reason = metadata.get("reason");

        return String.format("Votre réservation de %s a été rejetée. Raison: %s",
                equipmentName, reason);
    }

    private String generateReservationCancelledMessage(Map<String, String> metadata, User actor) {
        String equipmentName = metadata.get("equipmentName");
        String reason = metadata.get("reason");

        return String.format("Réservation de %s annulée. %s",
                equipmentName, reason != null ? "Raison: " + reason : "");
    }

    private String generateReservationStartedMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        return String.format("Votre réservation de %s a commencé", equipmentName);
    }

    private String generateReservationEndingSoonMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        return String.format("Votre réservation de %s se termine bientôt", equipmentName);
    }

    private String generateReservationOverdueMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        return String.format("Votre réservation de %s est en retard. Veuillez retourner l'équipement.", equipmentName);
    }

    private String generateMaintenanceMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        String description = metadata.get("description");
        return String.format("Maintenance requise pour %s: %s", equipmentName, description);
    }

    private String generateMaintenanceHighMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        String description = metadata.get("description");
        return String.format("⚠️ Maintenance prioritaire pour %s: %s", equipmentName, description);
    }

    private String generateMaintenanceUrgentMessage(Map<String, String> metadata) {
        String equipmentName = metadata.get("equipmentName");
        String description = metadata.get("description");
        return String.format("🚨 URGENT - Maintenance requise pour %s: %s", equipmentName, description);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "";
        try {
            return dateStr.substring(0, 10); // Format YYYY-MM-DD
        } catch (Exception e) {
            return dateStr;
        }
    }
}