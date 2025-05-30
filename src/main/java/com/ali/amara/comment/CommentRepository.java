package com.ali.amara.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.post.id = :postId AND c.parentComment IS NULL")
    List<Comment> findByPostIdWithReplies(@Param("postId") Long postId);

    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);
}
