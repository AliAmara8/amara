package com.ali.amara.comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String authorName;
    private LocalDateTime createdAt;
    private List<CommentDto> replies;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CommentDto> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDto> replies) {
        this.replies = replies;
    }
}
