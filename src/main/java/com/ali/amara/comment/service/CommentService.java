package com.ali.amara.comment.service;

import com.ali.amara.comment.dto.CommentRequest;
import com.ali.amara.comment.dto.CommentResponse;
import com.ali.amara.comment.entity.Comment;
import com.ali.amara.comment.mapper.CommentMapper;
import com.ali.amara.comment.repository.CommentRepository;
import com.ali.amara.notification.NotificationType;
import com.ali.amara.notification.dto.CreateNotificationRequest;
import com.ali.amara.notification.service.NotificationService;
import com.ali.amara.post.entity.Post;
import com.ali.amara.post.repository.PostRepository;
import com.ali.amara.security.service.CurrentUserService;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse createComment(CommentRequest request, User authenticatedUser) {
        validateCommentRequest(request);

        Post post = fetchPostOrThrow(request.postId());
        Comment parentComment = fetchParentCommentIfExists(request.parentId());

        Comment comment = buildNewComment(request, authenticatedUser, post, parentComment);

        if (parentComment != null) {
            // C'est une réponse, on met à jour le compteur du commentaire parent.
            parentComment.addReply(comment);
            // La transaction s'occupera de sauvegarder ce changement sur parentComment.
        } else {
            // === MISE À JOUR DU COMPTEUR DU POST ===
            // C'est un commentaire principal, on met à jour le compteur du post.
            post.setCommentCount(post.getCommentCount() + 1);
            // Pas besoin de save ici, l'entité 'post' est managée par la transaction.
            // Les changements seront persistés à la fin de la méthode.
        }

        Comment savedComment = commentRepository.save(comment);

// === GESTION SÉCURISÉE DES NOTIFICATIONS ===
        try {
            if (parentComment != null) {
                // C'est une réponse. On notifie l'auteur du commentaire parent.
                User parentAuthor = parentComment.getAuthor();
                if (!parentAuthor.getId().equals(authenticatedUser.getId())) {
                    CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                            .recipientId(parentAuthor.getId())
                            .actorId(authenticatedUser.getId())
                            .type(NotificationType.REPLY)
                            .message(authenticatedUser.getFullName() + " a répondu à votre commentaire.")
                            .entityType("COMMENT")
                            .entityId(parentComment.getId())
                            .link("/posts/" + post.getId() + "?comment=" + parentComment.getId())
                            .build();
                    notificationService.createNotification(notificationRequest);
                }
            } else {
                // C'est un commentaire principal. On notifie l'auteur du post.
                User postAuthor = post.getAuthor();
                if (!postAuthor.getId().equals(authenticatedUser.getId())) {
                    CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                            .recipientId(postAuthor.getId())
                            .actorId(authenticatedUser.getId())
                            .type(NotificationType.COMMENT)
                            .message(authenticatedUser.getFullName() + " a commenté votre publication.")
                            .entityType("POST")
                            .entityId(post.getId())
                            .link("/posts/" + post.getId())
                            .build();
                    notificationService.createNotification(notificationRequest);
                }
            }
        } catch (Exception e) {
            // Si la création de la notification échoue, on ne bloque pas tout.
            // On logue l'erreur pour analyse ultérieure, mais l'opération principale (création du commentaire) réussit.
            log.error("Échec de la création de la notification pour le commentaire ID {}. Le commentaire a bien été créé.",
                    savedComment.getId(), e);
        }
// =======================================================

        return commentMapper.toResponse(savedComment, authenticatedUser);
    }

    @Transactional
    public CommentResponse createReply(Long userId, Long postId, Long parentCommentId, String content, String imageUrl) {
        User user = fetchUserOrThrow(userId);
        Post post = fetchPostOrThrow(postId);
        Comment parentComment = fetchCommentOrThrow(parentCommentId);

        Comment reply = Comment.builder()
                .content(content)
                .imageUrl(imageUrl)
                .author(user)
                .post(post)
                .parent(parentComment)
                .build(); // L'entité initialise les compteurs à 0 par défaut

        // La logique est dans l'entité
        parentComment.addReply(reply);

        Comment savedReply = commentRepository.save(reply);

        return commentMapper.toResponse(savedReply, user);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        User currentUser = getCurrentUserOrNull(); // Utilise la méthode d'aide

        List<Comment> comments = commentRepository.findByPostIdAndParentIsNull(postId);

        return commentMapper.toResponseList(comments, currentUser); // Utilise la méthode du mapper pour les listes
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getRepliesForComment(Long commentId) {
        User currentUser = getCurrentUserOrNull();
        Comment parent = fetchCommentOrThrow(commentId);

        // On passe la liste des réponses et l'utilisateur au mapper
        return commentMapper.toResponseList(parent.getReplies(), currentUser);
    }

    @Transactional
    public CommentResponse likeComment(Long commentId, User currentUser) {
        // 1. Récupérer le commentaire à liker
        Comment comment = fetchCommentOrThrow(commentId);

        // 2. Utiliser la logique métier de l'entité pour ajouter le like
        //    (Ceci met à jour la collection 'likedBy' et le compteur 'likesCount')
        comment.addLike(currentUser);

        // 3. Sauvegarder les changements
        Comment savedComment = commentRepository.save(comment);

        // === GESTION DE LA NOTIFICATION DE LIKE ===
        User commentAuthor = comment.getAuthor();

        // On notifie l'auteur du commentaire, sauf s'il se like lui-même.
        if (!commentAuthor.getId().equals(currentUser.getId())) {

            CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                    .recipientId(commentAuthor.getId())
                    .actorId(currentUser.getId())
                    .type(NotificationType.REACTION) // En supposant que vous avez un type LIKE dans votre enum
                    .message(currentUser.getFullName() + " a aimé votre commentaire.")
                    .entityType("COMMENT") // L'entité liée est le commentaire
                    .entityId(comment.getId())
                    // Lien qui mène au post, et peut-être ancre sur le commentaire
                    .link("/posts/" + comment.getPost().getId() + "#comment-" + comment.getId())
                    .build();

            notificationService.createNotification(notificationRequest);
            log.info("LIKE notification created for user {} from user {}", commentAuthor.getId(), currentUser.getId());
        }
        // =========================================

        // 4. Retourner le DTO mis à jour, en passant le currentUser pour que le champ "liked" soit "true"
        return commentMapper.toResponse(savedComment, currentUser);
    }

    @Transactional
    public CommentResponse unlikeComment(Long commentId, User currentUser) {
        Comment comment = fetchCommentOrThrow(commentId);

        // On utilise la logique métier encapsulée dans l'entité
        comment.removeLike(currentUser);

        Comment savedComment = commentRepository.save(comment);

        // On appelle directement le mapper pour créer la réponse.
        return commentMapper.toResponse(savedComment, currentUser);
    }

    @Transactional(readOnly = true)
    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        // 1. Récupérer le commentaire à supprimer
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        // 2. Vérifier les permissions
        // (Votre logique avec isAdmin et isModerator est parfaite)
        if (!comment.getAuthor().getId().equals(currentUser.getId()) && !currentUser.isAdmin() && !currentUser.isModerator()) {
            throw new AccessDeniedException("You do not have permission to delete this comment");
        }

        // --- MISE À JOUR DES COMPTEURS AVANT SUPPRESSION ---

        // Si c'est un commentaire principal (pas une réponse), on décrémente le compteur du Post.
        if (comment.getParent() == null) {
            Post post = comment.getPost();
            if (post != null) {
                post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                // Pas besoin de save, la transaction s'en occupera.
            }
        }

        // La décrémentation du compteur de réponses du parent est déjà gérée par le @PreRemove
        // dans l'entité Comment. Nous n'avons donc rien à faire pour ce cas ici.

        // 4. Supprimer le commentaire
        // La suppression va déclencher l'événement @PreRemove sur l'entité Comment.
        commentRepository.delete(comment);

        log.info("Comment {} deleted by user {}", commentId, currentUser.getId());
    }

    // Méthodes helper privées
    private void validateCommentRequest(CommentRequest request) {
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
    }

    private Post fetchPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
    }

    private User fetchUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private Comment fetchCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
    }

    private Comment fetchParentCommentIfExists(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return fetchCommentOrThrow(parentId);
    }

    private Comment buildNewComment(CommentRequest request, User author, Post post, Comment parent) {
        return Comment.builder()
                .content(request.content())
                .imageUrl(request.imageUrl())
                .post(post)
                .author(author)
                .parent(parent)
                .likesCount(0)
                .repliesCount(0)
                .build();
    }

    private void incrementRepliesCount(Comment comment) {
        comment.setRepliesCount(comment.getRepliesCount() + 1);
        commentRepository.save(comment);
    }

    private void decrementRepliesCount(Comment comment) {
        comment.setRepliesCount(Math.max(0, comment.getRepliesCount() - 1));
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentReplies(Long commentId, Pageable pageable) {
        // 1. On vérifie que le commentaire parent existe
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Parent comment not found with id: " + commentId);
        }

        // 2. On récupère l'utilisateur courant (peut être null pour les visiteurs)
        User currentUser = getCurrentUserOrNull();

        // 3. On récupère la page de réponses
        Page<Comment> repliesPage = commentRepository.findByParentId(commentId, pageable);

        // 4. On mappe la page d'entités en page de DTOs en passant le contexte de l'utilisateur
        return repliesPage.map(reply -> commentMapper.toResponse(reply, currentUser));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsForPost(Long postId, Pageable pageable) {
        // 1. Récupérer l'utilisateur courant. Il peut être null (visiteur anonyme).
        User currentUser = getCurrentUserOrNull();

        // 2. Récupérer la page d'entités Comment depuis la base de données.
        Page<Comment> commentPage = commentRepository.findByPostIdAndParentIsNull(postId, pageable);

        // 3. Mapper la page d'entités en page de DTOs en utilisant une lambda.
        return commentPage.map(comment -> commentMapper.toResponse(comment, currentUser));
    }

    @Transactional
    public CommentResponse addReplyToComment(Long parentCommentId, CommentRequest request, User author) {
        // 1. Récupérer le commentaire parent
        Comment parentComment = fetchCommentOrThrow(parentCommentId);

        // 2. Récupérer le post associé (bonne pratique pour s'assurer que tout est cohérent)
        Post post = parentComment.getPost();

        // 3. Construire la nouvelle entité "réponse"
        Comment reply = buildNewComment(request, author, post, parentComment);

        // 4. Utiliser la logique de l'entité pour ajouter la réponse.
        //    La méthode addReply dans l'entité Comment devrait gérer l'ajout à la liste ET l'incrémentation du compteur.
        parentComment.addReply(reply);

        // 5. Sauvegarder la nouvelle réponse.
        //    Grâce à la cascade, la mise à jour du compteur sur `parentComment` sera aussi sauvegardée.
        Comment savedReply = commentRepository.save(reply);

        // === AJOUT DE LA LOGIQUE DE NOTIFICATION ===
        User parentAuthor = parentComment.getAuthor();
        // On notifie l'auteur du commentaire parent, sauf si c'est la même personne
        if (!parentAuthor.getId().equals(author.getId())) {
            CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                    .recipientId(parentAuthor.getId())
                    .actorId(author.getId())
                    .type(NotificationType.REPLY)
                    .message(author.getFullName() + " a répondu à votre commentaire.")
                    .entityType("COMMENT")
                    .entityId(parentComment.getId())
                    .link("/posts/" + post.getId() + "?comment=" + parentComment.getId()) // Lien direct
                    .build();

            notificationService.createNotification(notificationRequest);
            log.info("Notification created for user {} about a reply from user {}", parentAuthor.getId(), author.getId());
        }
        // ===========================================


        // 6. Utiliser le mapper pour créer la réponse
        return commentMapper.toResponse(savedReply, author);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, @Valid CommentRequest request, User currentUser) {
        log.debug("Attempting to update comment {} by user {}", commentId, currentUser.getId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Update failed: Comment not found with id: {}", commentId);
                    return new EntityNotFoundException("Comment not found");
                });

        // --- VÉRIFICATION DE PERMISSIONS AMÉLIORÉE ---
        boolean isAdmin = currentUser.isAdmin(); // En supposant que User a une méthode isAdmin()
        boolean isModerator = currentUser.isModerator(); // En supposant que User a une méthode isModerator()

        if (!comment.getAuthor().getId().equals(currentUser.getId()) && !isAdmin && !isModerator) {
            log.warn("FORBIDDEN: User {} tried to update comment {} owned by {}",
                    currentUser.getId(), commentId, comment.getAuthor().getId());
            throw new AccessDeniedException("You do not have permission to edit this comment");
        }
        // ---------------------------------------------

        // On ne met à jour le contenu que s'il est fourni dans la requête.
        if (request.content() != null) {
            // On peut ajouter une validation ici : si le contenu est une chaîne vide, on le refuse ?
            if (request.content().isBlank()) {
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }
            comment.setContent(request.content());
        }

        // La gestion de l'image est plus complexe. Si on envoie une nouvelle image,
        // l'ancienne doit-elle être supprimée ? Pour l'instant, on met juste à jour l'URL.
        if (request.imageUrl() != null) {
            // TODO: Ajouter une logique pour supprimer l'ancienne image du stockage si elle existe.
            comment.setImageUrl(request.imageUrl());
        }

        // L'annotation @LastModifiedDate sur le champ updatedAt de BaseEntity devrait gérer ça automatiquement.
        // Si ce n'est pas le cas, la ligne ci-dessous est correcte.
        // comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} successfully updated by user {}", commentId, currentUser.getId());

        // On appelle directement le mapper avec le contexte de l'utilisateur.
        return commentMapper.toResponse(updatedComment, currentUser);
    }

    // Assurez-vous d'avoir cette méthode d'aide dans votre service :
    private User getCurrentUserOrNull() {
        try {
            // Suppose que vous avez un service pour ça, sinon utilisez SecurityContextHolder
            return currentUserService.getCurrentUser();
        } catch (IllegalStateException | ClassCastException e) {
            // Cela arrive si aucun utilisateur n'est authentifié
            return null;
        }
    }

}