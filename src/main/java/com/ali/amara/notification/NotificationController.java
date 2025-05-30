package com.ali.amara.notification;


import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            List<Notification> notifications = notificationRepository.findByUser(user);
            List<NotificationDTO> dtos = notifications.stream()
                    .map(n -> new NotificationDTO(n.getId(), n.getUser().getId(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
            notification.setRead(true);
            notificationRepository.save(notification);
            return ResponseEntity.ok("Notification marquée comme lue");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}