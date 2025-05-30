package com.ali.amara.relationship;

import com.ali.amara.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "relationships")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private UserEntity friend;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    private LocalDateTime createdAt;

    // Getters, setters, constructeurs

    public Relationship(Long id, UserEntity user, UserEntity friend, RelationshipStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.friend = friend;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Relationship() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getFriend() {
        return friend;
    }

    public void setFriend(UserEntity friend) {
        this.friend = friend;
    }

    public RelationshipStatus getStatus() {
        return status;
    }

    public void setStatus(RelationshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
