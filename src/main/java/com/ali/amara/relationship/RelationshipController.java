package com.ali.amara.relationship;

import com.ali.amara.user.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;
    private static final Logger log = LoggerFactory.getLogger(RelationshipController.class);
    // Envoyer une demande d'ami
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @RequestParam Long userId,
            @RequestParam Long friendId) {

        // Créer une map pour la réponse JSON
        Map<String, Object> response = new HashMap<>();

        try {
            // Appeler le service pour envoyer la demande d'ami
            RelationshipDTO relationship = relationshipService.sendFriendRequest(userId, friendId);

            // Construire la réponse JSON en cas de succès
            response.put("success", true);
            response.put("message", "Demande d'ami envoyée avec succès");
            response.put("data", relationship); // Inclure les détails de la relation

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        } catch (RuntimeException e) {
            // Gérer les exceptions et retourner une réponse d'erreur structurée
            response.put("success", false);
            response.put("message", e.getMessage()); // Message d'erreur
            response.put("data", null); // Aucune donnée en cas d'erreur

            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }

    // Accepter une demande
    @PutMapping("/accept")
    public ResponseEntity<RelationshipDTO> acceptFriendRequest(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        try {
            RelationshipDTO relationship = relationshipService.acceptFriendRequest(userId, friendId);
            return ResponseEntity.ok(relationship);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    // Supprimer un ami
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFriend(
            @RequestParam Long userId,
            @RequestParam Long friendId) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Tentative de suppression de la relation entre userId={} et friendId={}", userId, friendId);

            // Appeler le service et récupérer le DTO
            RelationshipDTO dto = relationshipService.removeFriend(userId, friendId);

            // Construire la réponse JSON
            response.put("success", true);
            response.put("message", "Ami supprimé avec succès");
            response.put("data", dto); // Ajouter le DTO dans la réponse

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }

    // blocker un utilisateur
    @PutMapping("/block")
    public ResponseEntity<RelationshipDTO> blockUser(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        try {
            RelationshipDTO relationship = relationshipService.blockUser(userId, friendId);
            return ResponseEntity.ok(relationship);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // annulé une demande
    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelFriendRequest(
            @RequestParam Long userId,
            @RequestParam Long friendId) {

        // Créer une map pour la réponse JSON
        Map<String, Object> response = new HashMap<>();
        try {
            RelationshipDTO relationship =relationshipService.rejectFriendRequest(userId, friendId);
            // Construire la réponse JSON en cas de succès
            response.put("success", true);
            response.put("message", "Demande d'ami annulée avec succès");
            response.put("data", relationship); // Inclure les détails de la relation

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (RuntimeException e) {
            // Gérer les exceptions et retourner une réponse d'erreur structurée
            response.put("success", false);
            response.put("message", e.getMessage()); // Message d'erreur
            response.put("data", null); // Aucune donnée en cas d'erreur

            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }

    // Liste des amis
    @GetMapping("/friends/{userId}")
    public ResponseEntity<List<UserDTO>> getFriends(
            @PathVariable Long userId) {
        try {
            List<UserDTO> friends = relationshipService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // Liste des demandes en attente
    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<UserDTO>> getPendingRequests(
            @PathVariable Long userId) {
        try {
            List<UserDTO> pendingRequests = relationshipService.getPendingRequests(userId);
            return ResponseEntity.ok(pendingRequests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @GetMapping("/suggest/{userId}")
    public ResponseEntity<List<UserDTO>> suggestUsers(
            @PathVariable Long userId) {
        try {
            log.info("Récupération des utilisateurs suggérés pour l'utilisateur {}", userId);

            // Logique pour récupérer les utilisateurs suggérés
            List<UserDTO> suggestedUsers = relationshipService.suggestUsers(userId);
            log.info("Suggested Users: {}", suggestedUsers);
            if (suggestedUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
            }

            return ResponseEntity.ok(suggestedUsers);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des utilisateurs suggérés: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}