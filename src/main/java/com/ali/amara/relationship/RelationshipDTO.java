package com.ali.amara.relationship;

import com.ali.amara.user.UserDTO;

import java.time.LocalDateTime;

public class RelationshipDTO {
    private Long id;
    private UserDTO user;
    private UserDTO friend;
    private String status;
    private LocalDateTime createdAt;

    // Constructeur par défaut
    public RelationshipDTO() {}

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public UserDTO getFriend() { return friend; }
    public void setFriend(UserDTO friend) { this.friend = friend; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


}
