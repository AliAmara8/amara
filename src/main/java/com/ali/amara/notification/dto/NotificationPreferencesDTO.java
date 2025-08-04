package com.ali.amara.notification.dto;

import com.ali.amara.notification.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NotificationPreferencesDTO {

    private Long userId;

    private boolean emailEnabled;

    private boolean pushEnabled;

    private boolean inAppEnabled;

    private Map<NotificationType.NotificationCategory, Boolean> categoryPreferences;

    private Map<NotificationType, Boolean> typePreferences;

    private String emailFrequency; // IMMEDIATE, DAILY, WEEKLY

    private boolean quietHours;

    private LocalDateTime quietHoursStart;

    private LocalDateTime quietHoursEnd;
}
