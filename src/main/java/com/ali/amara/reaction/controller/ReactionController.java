package com.ali.amara.reaction.controller;

import com.ali.amara.reaction.dto.ReactionRequest;
import com.ali.amara.reaction.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reactions", description = "Endpoints pour gérer les réactions sur les posts et commentaires")
public class ReactionController {

    private final ReactionService reactionService;

    // --- Réactions sur les Posts ---

    @PostMapping("/posts/{postId}/reactions")
    @Operation(summary = "Ajouter ou modifier une réaction sur un post")
    public ResponseEntity<Void> reactToPost(@PathVariable Long postId, @Valid @RequestBody ReactionRequest request) {
        reactionService.addOrUpdatePostReaction(postId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/reactions")
    @Operation(summary = "Supprimer sa réaction sur un post")
    public ResponseEntity<Void> removeReactionFromPost(@PathVariable Long postId) {
        reactionService.removePostReaction(postId);
        return ResponseEntity.noContent().build();
    }

    // --- Réactions sur les Commentaires ---

    @PostMapping("/comments/{commentId}/reactions")
    @Operation(summary = "Ajouter ou modifier une réaction sur un commentaire")
    public ResponseEntity<Void> reactToComment(@PathVariable Long commentId, @Valid @RequestBody ReactionRequest request) {
        reactionService.addOrUpdateCommentReaction(commentId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}/reactions")
    @Operation(summary = "Supprimer sa réaction sur un commentaire")
    public ResponseEntity<Void> removeReactionFromComment(@PathVariable Long commentId) {
        reactionService.removeCommentReaction(commentId);
        return ResponseEntity.noContent().build();
    }
}