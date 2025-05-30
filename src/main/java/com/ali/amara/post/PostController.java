package com.ali.amara.post;

import com.ali.amara.config.FileStorageService;
import com.ali.amara.relationship.RelationshipController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.util.List;




@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository; // Injection de UserRepository
    private static final Logger log = LoggerFactory.getLogger(PostController.class);


    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("post") String postDTOString, // Accept the JSON string
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request
    ) {
        // Log the incoming request
        log.debug("Received postDTOString: {}", postDTOString);
        if (file != null) {
            log.debug("Received file: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        }

        // Deserialize the JSON string into a PostDTO object
        ObjectMapper objectMapper = new ObjectMapper();
        PostDTO postDTO;
        try {
            postDTO = objectMapper.readValue(postDTOString, PostDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse PostDTO: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Log the deserialized PostDTO
        log.debug("Deserialized PostDTO: {}", postDTO);

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Authenticated user: {}", username);

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", username);
                    return new RuntimeException("User not found");
                });
        Long userId = user.getId();

        // Téléverser le fichier si présent
        if (file != null && !file.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(file, "postpic");
            postDTO.setImageUrl(fileUrl);
        }

        // Créer le post via le service
        PostDTO createdPost = postService.createPost(postDTO, userId);
        log.debug("Post created successfully: {}", createdPost);

        // Renvoyer la réponse
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }


    @PostMapping("/test-upload")
    public ResponseEntity<String> testUpload(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        log.debug("Received data: {}", data);
        if (file != null) {
            log.debug("Received file: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        }
        return new ResponseEntity<>("Upload successful", HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost) {
        Post post = postService.updatePost(postId, updatedPost);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}