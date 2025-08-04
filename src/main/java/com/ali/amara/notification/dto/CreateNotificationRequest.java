package com.ali.amara.notification.dto;

import com.ali.amara.notification.NotificationType;
import com.ali.amara.notification.entity.Notification;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull
    private Long recipientId;

    private Long actorId;

    @NotNull
    private NotificationType type;

    private String message;

    private String link;

    private Long entityId;

    private String entityType;

    private Map<String, String> metadata;

    private LocalDateTime scheduledAt;

    private Notification.NotificationPriority priority;

    private boolean sendEmail = false;

    private boolean sendPush = false;
}
