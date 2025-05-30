package com.ali.amara.chat;


import com.ali.amara.chat.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour gérer les messages dans le chat d'Agrimate.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Envoie un message entre deux utilisateurs.
     * @param senderId ID de l’expéditeur
     * @param receiverId ID du destinataire
     * @param content Contenu textuel (optionnel)
     * @param imageUrl URL de l’image (optionnel)
     * @param fileUrl URL du fichier (optionnel)
     * @return ResponseEntity avec le MessageDTO ou une erreur
     */
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String fileUrl) {
        try {
            MessageDTO message = chatService.sendMessage(senderId, receiverId, content, imageUrl, fileUrl);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Récupère la conversation entre deux utilisateurs.
     * @param userId1 ID du premier utilisateur
     * @param userId2 ID du second utilisateur
     * @return ResponseEntity avec la liste des messages ou une erreur
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        try {
            List<MessageDTO> messages = chatService.getConversation(userId1, userId2);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}