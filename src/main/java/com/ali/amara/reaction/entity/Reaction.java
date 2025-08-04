package com.ali.amara.reaction.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.post.entity.Post;
import com.ali.amara.comment.entity.Comment;
import com.ali.amara.reaction.enums.ReactionType;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_user_post_reaction", columnNames = {"author_id", "post_id"}),
                @UniqueConstraint(name = "uc_user_comment_reaction", columnNames = {"author_id", "comment_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;
}