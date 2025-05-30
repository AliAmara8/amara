package com.ali.amara.comment;

import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likes;
    private UserDTO user;
    private Long postId;
    private List<CommentDto> replies; // Pour les réponses hiérarchiques
    private String imageUrl;

    // Constructeur principal
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.likes = comment.getLikes();
        this.postId = comment.getPost().getId();
        this.imageUrl = comment.getImageUrl();
        this.user = comment.getUser() != null ? new UserDTO(comment.getUser()) : null;

        // Conversion des réponses en DTOs
        this.replies = comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList())
                : null;
    }

    // Getters (obligatoires pour la sérialisation JSON)
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public UserDTO getUser() {
        return user;
    }

    public List<CommentDto> getReplies() {
        return replies;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setReplies(List<CommentDto> replies) {
        this.replies = replies;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}