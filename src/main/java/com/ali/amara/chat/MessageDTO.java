package com.ali.amara.chat;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String imageUrl;
    private String fileUrl;
    private String sentAt;
    private String status; // Changé en String pour correspondre à MessageStatus

    public MessageDTO() {}
    public MessageDTO(Long id, Long senderId, Long receiverId, String content, String imageUrl, String fileUrl, String sentAt, String status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.sentAt = sentAt;
        this.status = status;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}