package com.ali.amara.notification;


import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(Long userId, String message) {
        // Récupérer l'utilisateur
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer et sauvegarder la notification dans la base
        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);

        // Préparer le DTO pour WebSocket
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setUserId(userId);
        notificationDTO.setMessage(message);
        notificationDTO.setRead(false);
        notificationDTO.setCreatedAt(notification.getCreatedAt());

        // Envoyer via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notificationDTO);
    }
}