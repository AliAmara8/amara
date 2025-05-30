package com.ali.amara.profile;

import com.ali.amara.config.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FileStorageService fileStorageService;

    // Créer ou mettre à jour un profil
    @PostMapping("/{userId}")
    public ResponseEntity<ProfileDTO> saveOrUpdateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileDTO profileDTO) {

        ProfileDTO savedProfile = profileService.saveOrUpdateProfile(userId, profileDTO);
        return ResponseEntity.ok(savedProfile);
    }

    // Récupérer un profil
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long userId) {
        ProfileDTO profileDTO = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profileDTO);
    }

    // Supprimer un profil par l'ID de l'utilisateur
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfileByUserId(@PathVariable Long userId) {
        profileService.deleteProfileByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    // Télécharger une photo de profil
    @PatchMapping("/profilepic/{userId}")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "profilepic");
            profileService.updateProfilePicture(userId, fileUrl);
            return ResponseEntity.ok(fileUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Télécharger une photo de couverture
    @PatchMapping("/coverpic/{userId}")
    public ResponseEntity<String> uploadCoverPicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "coverpic");
            profileService.updateCoverPicture(userId, fileUrl);
            return ResponseEntity.ok(fileUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}