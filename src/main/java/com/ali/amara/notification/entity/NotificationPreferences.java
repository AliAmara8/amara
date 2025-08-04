package com.ali.amara.notification.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.notification.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "notification_preferences", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferences extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Builder.Default
    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = false;

    @Builder.Default
    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled = true;

    @Builder.Default
    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "notification_category_preferences",
            joinColumns = @JoinColumn(name = "preferences_id"))
    @MapKeyColumn(name = "category")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "enabled")
    @Builder.Default
    private Map<NotificationType.NotificationCategory, Boolean> categoryPreferences = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "notification_type_preferences",
            joinColumns = @JoinColumn(name = "preferences_id"))
    @MapKeyColumn(name = "type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "enabled")
    @Builder.Default
    private Map<NotificationType, Boolean> typePreferences = new HashMap<>();

    @Column(name = "email_frequency", length = 20)
    @Builder.Default
    private String emailFrequency = "DAILY";

    @Builder.Default
    @Column(name = "quiet_hours", nullable = false)
    private boolean quietHours = false;

    @Column(name = "quiet_hours_start")
    private LocalDateTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalDateTime quietHoursEnd;

    // Méthodes utilitaires
    public boolean isCategoryEnabled(NotificationType.NotificationCategory category) {
        return categoryPreferences.getOrDefault(category, true);
    }

    public boolean isTypeEnabled(NotificationType type) {
        return typePreferences.getOrDefault(type, true);
    }

    public void setCategoryEnabled(NotificationType.NotificationCategory category, boolean enabled) {
        categoryPreferences.put(category, enabled);
    }

    public void setTypeEnabled(NotificationType type, boolean enabled) {
        typePreferences.put(type, enabled);
    }
}