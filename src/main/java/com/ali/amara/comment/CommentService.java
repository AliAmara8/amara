package com.ali.amara.comment;

import com.ali.amara.exception.ResourceNotFoundException;
import com.ali.amara.post.Post;
import com.ali.amara.post.PostRepository;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// CommentService.java
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;





    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdWithReplies(postId); // Utilisez une méthode custom
        return comments.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public CommentResponse createComment(CreateCommentRequest request, UserEntity user) {
        // 1. Vérification du post associé
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));

        // 2. Création de l'entité
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setImageUrl(request.getImageUrl());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        // 3. Gestion des réponses (commentaires imbriqués)
        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Commentaire parent non trouvé"));
            comment.setParentComment(parent);
        }

        // 4. Sauvegarde
        Comment savedComment = commentRepository.save(comment);

        // 5. Conversion en DTO de réponse
        return convertToResponse(savedComment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setImageUrl(comment.getImageUrl());
        response.setAuthorName(comment.getUser().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

    public Comment addCommentToPost(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public Comment addReplyToComment(Long commentId, Long userId, String content) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(user);
        reply.setParentComment(parentComment);
        reply.setPost(parentComment.getPost());

        return commentRepository.save(reply);
    }

    public Comment likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setLikes(comment.getLikes() + 1);
        return commentRepository.save(comment);
    }
    

//    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable, UserEntity currentUser) {
//        // 1. Vérifier que le post existe
//        if (!postRepository.existsById(postId)) {
//            throw new ResourceNotFoundException("Post not found");
//        }
//
//        // 2. Récupérer les commentaires principaux (sans parent)
//        Page<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);
//
//        // 3. Convertir en DTO avec réponses imbriquées
//        return comments.map(comment -> {
//            CommentResponse response = convertToResponse(comment, currentUser);
//            response.setReplies(getNestedReplies(comment.getId(), currentUser));
//            return response;
//        });
//    }
//
//    private List<CommentResponse> getNestedReplies(Long parentId, UserEntity currentUser) {
//        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentId);
//        return replies.stream()
//                .map(reply -> convertToResponse(reply, currentUser))
//                .collect(Collectors.toList());
//    }
//
//    private CommentResponse convertToResponse(Comment comment, UserEntity currentUser) {
//        return CommentResponse.builder()
//                .id(comment.getId())
//                .content(comment.getContent())
//                .imageUrl(comment.getImageUrl())
//                .author(new AuthorInfo(
//                        comment.getUser().getId(),
//                        comment.getUser().getUsername(),
//                        comment.getUser().getAvatarUrl()
//                ))
//                .createdAt(comment.getCreatedAt())
//                .likesCount(comment.getLikes().size())
//                .isLikedByCurrentUser(isLikedByUser(comment, currentUser))
//                .replies(convertReplies(comment.getReplies(), currentUser))
//                .build();
//    }
//
//    private boolean isLikedByUser(Comment comment, UserEntity user) {
//        return comment.getLikedBy().contains(user);
//    }
//
//    private List<CommentResponse> convertReplies(List<Comment> replies, UserEntity user) {
//        return replies.stream()
//                .map(reply -> convertToResponse(reply, user))
//                .toList();
//    }
}
