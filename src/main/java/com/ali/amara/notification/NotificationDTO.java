package com.ali.amara.notification;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    // Constructeurs
    public NotificationDTO() {}
    public NotificationDTO(Long id, Long userId, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
