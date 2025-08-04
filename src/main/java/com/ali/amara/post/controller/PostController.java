package com.ali.amara.post.controller;

import com.ali.amara.post.dto.PostDTO;
import com.ali.amara.post.dto.ShareRequestDTO;
import com.ali.amara.post.service.PostService;
import com.ali.amara.post.entity.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createPost( // On peut utiliser un type générique pour la réponse d'erreur
                                         @RequestPart("post") String postDtoString, // <-- ON REÇOIT LE JSON COMME UN STRING
                                         @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                         @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        PostDTO postDTO;
        try {
            // On convertit manuellement la chaîne en DTO
            postDTO = objectMapper.readValue(postDtoString, PostDTO.class);
        } catch (Exception e) {
            log.error("Error parsing PostDTO from JSON string: {}", postDtoString, e);
            return new ResponseEntity<>("Invalid post data format.", HttpStatus.BAD_REQUEST);
        }

        // On appelle le service avec les objets maintenant correctement formés
        PostDTO createdPost = postService.create(postDTO, files);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable Long id) {
        postService.incrementViewCount(id); // Incrémente les vues
        return ResponseEntity.ok(postService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestPart("post") PostDTO postDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        PostDTO updatedPost = postService.update(id, postDTO, files);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<PostDTO>> getPostsByAuthor(
            @PathVariable Long authorId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.findByAuthor(authorId, pageable));
    }

    @GetMapping("/type/{postType}")
    public ResponseEntity<List<PostDTO>> getPostsByType(
            @PathVariable PostType postType) {
        return ResponseEntity.ok(postService.findByPostType(postType));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<PostDTO>> getPostsByTags(
            @RequestParam Set<String> tags) {
        return ResponseEntity.ok(postService.findByTags(tags));
    }

    @GetMapping("/crops")
    public ResponseEntity<List<PostDTO>> getPostsByCrops(
            @RequestParam Set<String> crops) {
        return ResponseEntity.ok(postService.findByCrops(crops));
    }

    @GetMapping("/farming-types")
    public ResponseEntity<List<PostDTO>> getPostsByFarmingTypes(
            @RequestParam Set<String> farmingTypes) {
        return ResponseEntity.ok(postService.findByFarmingTypes(farmingTypes));
    }

    @GetMapping("/location")
    public ResponseEntity<List<PostDTO>> getPostsByLocation(
            @RequestParam String location) {
        return ResponseEntity.ok(postService.findByLocation(location));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<PostDTO> sharePost(
            @PathVariable Long id,
            @RequestBody(required = false) ShareRequestDTO shareRequest // Le corps est optionnel
    ) {
        // On extrait le contenu du DTO, qui peut être null si le corps de la requête est vide
        String content = (shareRequest != null) ? shareRequest.getContent() : null;

        // On appelle la méthode du service avec les deux arguments
        PostDTO sharedPost = postService.sharePost(id, content);

        // On retourne une réponse 201 Created avec le nouveau post créé
        return new ResponseEntity<>(sharedPost, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> savePost(
            @PathVariable Long id) {
        postService.savePost(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsavePost(
            @PathVariable Long id) {
        postService.unsavePost(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostDTO>> getUserFeed(
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.findFeed(userId, pageable));
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<PostDTO>> getTrendingPosts(
            Pageable pageable) {
        return ResponseEntity.ok(postService.findTrending(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDTO>> searchPosts(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPostsByContent(q, pageable));
    }
}