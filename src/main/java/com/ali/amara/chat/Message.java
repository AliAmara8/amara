package com.ali.amara.chat;

import com.ali.amara.user.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Column
    private String content;

    @Column
    private String imageUrl;

    @Column
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    public Message() {}
    public Message(UserEntity sender, UserEntity receiver, String content, String imageUrl, String fileUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getSender() { return sender; }
    public void setSender(UserEntity sender) { this.sender = sender; }
    public UserEntity getReceiver() { return receiver; }
    public void setReceiver(UserEntity receiver) { this.receiver = receiver; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
}



