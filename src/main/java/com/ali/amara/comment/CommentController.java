package com.ali.amara.comment;

import com.ali.amara.config.FileStorageService;
import com.ali.amara.post.PostController;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// CommentController.java
@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")

@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository; // Injection de UserRepository
    @Autowired
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentResponse> createComment(
            @RequestPart("comment") String commentRequestJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {

        // 1. Journalisation
        log.info("Requête de création de commentaire reçue");
        if (file != null) {
            log.info("Fichier joint: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        }

        // 2. Conversion JSON -> Objet
        CreateCommentRequest commentRequest;
        try {
            commentRequest = objectMapper.readValue(commentRequestJson, CreateCommentRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Erreur de parsing JSON", e);
            return ResponseEntity.badRequest().build();
        }

        // 3. Récupération utilisateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 4. Gestion du fichier
        if (file != null && !file.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(file, "comments/" + user.getId());
            commentRequest.setImageUrl(fileUrl);
        }

        // 5. Création du commentaire
        CommentResponse response = commentService.createComment(commentRequest, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsForPost(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<Comment> addReplyToComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestBody String content) {
        Comment reply = commentService.addReplyToComment(commentId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable Long commentId) {
        Comment comment = commentService.likeComment(commentId);
        return ResponseEntity.ok(comment);
    }
}
