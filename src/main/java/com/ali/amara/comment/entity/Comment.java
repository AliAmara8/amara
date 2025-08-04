package com.ali.amara.comment.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.post.entity.Post;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_post_id", columnList = "post_id"),
    @Index(name = "idx_comment_author_id", columnList = "author_id"),
    @Index(name = "idx_comment_parent_id", columnList = "parent_id")
})
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Builder.Default
    @Column(name = "likes_count")
    private int likesCount = 0;

    @Builder.Default
    @Column(name = "replies_count")
    private int repliesCount = 0;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "comment_likes",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"),
        indexes = {
            @Index(name = "idx_comment_likes_comment", columnList = "comment_id"),
            @Index(name = "idx_comment_likes_user", columnList = "user_id")
        }
    )
    private Set<User> likedBy = new HashSet<>();

    @PreRemove
    private void preRemove() {
        // Mise à jour du compteur de réponses du parent lors de la suppression
        if (parent != null) {
            parent.setRepliesCount(parent.getRepliesCount() - 1);
        }
    }

    public void addLike(User user) {
        if (likedBy.add(user)) {
            likesCount++;
        }
    }

    public void removeLike(User user) {
        if (likedBy.remove(user)) {
            likesCount--;
        }
    }

    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
        this.repliesCount = this.replies.size();
    }

    public void removeReply(Comment reply) {
        if (this.replies != null) {
            this.replies.remove(reply);
            this.repliesCount = this.replies.size();
        }
    }
}
