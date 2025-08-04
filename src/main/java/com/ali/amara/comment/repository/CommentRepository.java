package com.ali.amara.comment.repository;

import com.ali.amara.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Fonctionnalités de base pour les posts
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    List<Comment> findByAuthorId(Long authorId);
    
    // Gestion de la hiérarchie des commentaires
    List<Comment> findByPostIdAndParentIsNull(Long postId);
    Page<Comment> findByPostIdAndParentIsNull(Long postId, Pageable pageable);
    List<Comment> findByParentId(Long parentId);
    Page<Comment> findByParentId(Long parentId, Pageable pageable);

    // Requêtes personnalisées pour les statistiques
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> findLatestCommentsByPostId(@Param("postId") Long postId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    // Requêtes pour la modération et l'analyse
    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId AND c.createdAt >= :since ORDER BY c.createdAt DESC")
    Page<Comment> findRecentCommentsByAuthor(@Param("authorId") Long authorId, @Param("since") LocalDateTime since, Pageable pageable);
}