package com.ali.amara.reaction.service;

import com.ali.amara.comment.entity.Comment;
import com.ali.amara.comment.repository.CommentRepository;
import com.ali.amara.notification.NotificationType;
import com.ali.amara.notification.dto.CreateNotificationRequest;
import com.ali.amara.notification.service.NotificationService;
import com.ali.amara.post.entity.Post;
import com.ali.amara.post.repository.PostRepository;
import com.ali.amara.reaction.dto.ReactionRequest;
import com.ali.amara.reaction.entity.Reaction;
import com.ali.amara.reaction.repository.ReactionRepository;
import com.ali.amara.security.service.CurrentUserService;
import com.ali.amara.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    // --- Logique pour les Réactions sur les Posts ---

    @Transactional
    public void addOrUpdatePostReaction(Long postId, ReactionRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        Optional<Reaction> existingReactionOpt = reactionRepository.findByAuthor_IdAndPost_Id(currentUser.getId(), postId);

        int scoreChange = 0;
        boolean isNewReaction = !existingReactionOpt.isPresent();

        if (existingReactionOpt.isPresent()) {
            Reaction existingReaction = existingReactionOpt.get();
            scoreChange -= existingReaction.getType().getEngagementScore();
            existingReaction.setType(request.getType());
            scoreChange += request.getType().getEngagementScore();
            reactionRepository.save(existingReaction);
        } else {
            Reaction newReaction = Reaction.builder()
                    .author(currentUser)
                    .post(post)
                    .type(request.getType())
                    .build();
            scoreChange += request.getType().getEngagementScore();
            reactionRepository.save(newReaction);
            post.setReactionCount(post.getReactionCount() + 1);
        }

        post.setReactionScore(post.getReactionScore() + scoreChange);

        // === GESTION DE LA NOTIFICATION ===

        // On notifie l'auteur du post, sauf si c'est lui-même qui réagit.
        // On envoie une notification uniquement pour une NOUVELLE réaction positive,
        // pas pour un changement de réaction ou une réaction négative (choix de conception).
        User postAuthor = post.getAuthor();
        if (isNewReaction && !postAuthor.getId().equals(currentUser.getId()) && request.getType().getEngagementScore() > 0) {

            String message = currentUser.getFullName() + " a réagi à votre publication.";
            // On pourrait rendre le message plus spécifique :
            // String message = currentUser.getFullName() + " a " + request.getType().getDisplayName().toLowerCase() + " votre publication.";

            CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                    .recipientId(postAuthor.getId())
                    .actorId(currentUser.getId())
                    .type(NotificationType.REACTION) // On peut utiliser un type générique "LIKE" ou créer "REACTION"
                    .message(message)
                    .entityType("POST")
                    .entityId(post.getId())
                    .link("/posts/" + post.getId())
                    .build();

            notificationService.createNotification(notificationRequest);
            log.info("Reaction notification created for user {} from user {}", postAuthor.getId(), currentUser.getId());
        }
        // ===================================
    }

    @Transactional
    public void removePostReaction(Long postId) {
        User currentUser = currentUserService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        Reaction reaction = reactionRepository.findByAuthor_IdAndPost_Id(currentUser.getId(), postId)
                .orElseThrow(() -> new EntityNotFoundException("No reaction found from this user on this post."));

        post.setReactionScore(post.getReactionScore() - reaction.getType().getEngagementScore());
        post.setReactionCount(post.getReactionCount() - 1);

        reactionRepository.delete(reaction);
    }

    // --- Logique pour les Réactions sur les Commentaires ---

    @Transactional
    public void addOrUpdateCommentReaction(Long commentId, ReactionRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        Optional<Reaction> existingReactionOpt = reactionRepository.findByAuthor_IdAndComment_Id(currentUser.getId(), commentId);

        int scoreChange = 0; // Utiliser un score pour les commentaires est une option

        if (existingReactionOpt.isPresent()) {
            Reaction existingReaction = existingReactionOpt.get();
            // Mettre à jour le compteur de likes de l'entité Comment
            comment.removeLike(existingReaction.getAuthor()); // Annule l'ancien "like"
            existingReaction.setType(request.getType());
            comment.addLike(currentUser); // Ajoute le "nouveau"
            reactionRepository.save(existingReaction);
        } else {
            Reaction newReaction = Reaction.builder()
                    .author(currentUser)
                    .comment(comment)
                    .type(request.getType())
                    .build();
            reactionRepository.save(newReaction);
            comment.addLike(currentUser); // Utilise votre méthode d'aide existante
        }
    }

    @Transactional
    public void removeCommentReaction(Long commentId) {
        User currentUser = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        Reaction reaction = reactionRepository.findByAuthor_IdAndComment_Id(currentUser.getId(), commentId)
                .orElseThrow(() -> new EntityNotFoundException("No reaction found from this user on this comment."));

        comment.removeLike(currentUser); // Utilise votre méthode d'aide existante
        reactionRepository.delete(reaction);
    }
}