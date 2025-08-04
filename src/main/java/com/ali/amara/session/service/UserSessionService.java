package com.ali.amara.session.service; // Ou un package plus générique comme "user.service"

import com.ali.amara.notification.service.EmailService;
import com.ali.amara.session.entity.UserSession;
import com.ali.amara.session.repository.UserSessionRepository;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Cache pour la présence en temps réel : websocketSessionId -> userId
    private final Map<String, Long> activeWebsocketSessions = new ConcurrentHashMap<>();

    // ====================================================================
    // 1. GESTION DU CYCLE DE VIE DE LA SESSION (Login / Logout HTTP)
    // ====================================================================

    @Transactional
    public UserSession createSession(User user, HttpServletRequest request) {
        // Optionnel : Invalider les anciennes sessions pour cet utilisateur
        // invalidateAllSessionsForUser(user.getId());

        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // On vérifie si cette connexion est "nouvelle" AVANT de la sauvegarder
        checkForNewDeviceOrLocation(user, ipAddress, userAgent);

        UserSession newSession = UserSession.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginTime(LocalDateTime.now())
                .lastActivityTime(LocalDateTime.now())
                .isActive (true)
                .build();

        return userSessionRepository.save(newSession);
    }

    @Transactional
    public void terminateSession(Long sessionId, User currentUser) {
        UserSession session = userSessionRepository.findByIdAndUserId(sessionId, currentUser.getId())
                .orElseThrow(() -> new SecurityException("Session not found or permission denied."));

        session.setActive(false);
        session.setLogoutTime(LocalDateTime.now());
    }

    @Transactional
    public void invalidateAllSessionsForUser(Long userId) {
        // 1. UPDATE : Envoie une seule commande UPDATE directement à la base de données.
        userSessionRepository.invalidateAllActiveSessionsForUser(userId, LocalDateTime.now());
    }

    // ====================================================================
    // 2. GESTION DE LA PRÉSENCE EN TEMPS RÉEL (WebSockets)
    // ====================================================================

    @Transactional
    public void userConnectedViaWebSocket(User user, String websocketSessionId) {
        user.setOnlineStatus(true);
        user.setLastSeen(LocalDateTime.now());
        activeWebsocketSessions.put(websocketSessionId, user.getId());
        // On met aussi à jour la "lastActivityTime" de sa session la plus récente
        userSessionRepository.findTopByUserIdAndIsActiveTrueOrderByLoginTimeDesc(user.getId())
                .ifPresent(latestSession -> latestSession.setLastActivityTime(LocalDateTime.now()));
        log.info("User ID {} marked as ONLINE with WebSocket session {}", user.getId(), websocketSessionId);
    }

    @Transactional
    public void userDisconnectedViaWebSocket(String websocketSessionId) {
        Long userId = activeWebsocketSessions.remove(websocketSessionId);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                user.setOnlineStatus(false);
                log.info("User ID {} marked as OFFLINE due to WebSocket disconnect", userId);
            });
        }
    }

    @Transactional
    public void updateHeartbeat(String websocketSessionId) {
        Long userId = activeWebsocketSessions.get(websocketSessionId);
        if (userId != null) {
            userRepository.updateLastSeen(userId, LocalDateTime.now());
            // On met aussi à jour la session en base de données
            userSessionRepository.findTopByUserIdAndIsActiveTrueOrderByLoginTimeDesc(userId)
                    .ifPresent(latestSession ->
                            userSessionRepository.updateLastActivityTime(latestSession.getId(), LocalDateTime.now())
                    );
        }
    }

    @Scheduled(fixedRate = 60000) // Toutes les minutes
    @Transactional
    public void checkInactiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        userRepository.findOnlineUsersLastSeenBefore(threshold).forEach(user -> {
            user.setOnlineStatus(false);
            // On nettoie aussi le cache au cas où un disconnect n'aurait pas été reçu
            activeWebsocketSessions.entrySet().removeIf(entry -> entry.getValue().equals(user.getId()));
            log.info("User ID {} marked as OFFLINE due to inactivity", user.getId());
        });
    }

    // ====================================================================
    // 3. MÉTHODES DE LECTURE (utilisées par d'autres services/controllers)
    // ====================================================================

    public boolean isUserOnline(Long userId) {
        return userRepository.findById(userId)
                .map(User::isOnlineStatus)
                .orElse(false);
    }

    public LocalDateTime getLastSeen(Long userId) {
        return userRepository.findById(userId)
                .map(User::getLastSeen)
                .orElse(null);
    }

    // ====================================================================
    // 4. LOGIQUE PRIVÉE D'AIDE
    // ====================================================================

    private void checkForNewDeviceOrLocation(User user, String newIpAddress, String newUserAgent) {
        List<UserSession> recentSessions = userSessionRepository.findTop5ByUserIdOrderByLoginTimeDesc(user.getId());
        if (recentSessions.isEmpty()) return;

        boolean isNewDevice = recentSessions.stream().noneMatch(s -> areUserAgentsSimilar(s.getUserAgent(), newUserAgent));
        if (isNewDevice) {
            emailService.sendNewDeviceLoginNotification(user, newIpAddress, newUserAgent);
        }
    }

    private boolean areUserAgentsSimilar(String oldAgent, String newAgent) {
        return oldAgent != null && oldAgent.equals(newAgent);
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr.split(",")[0].trim() : request.getRemoteAddr();
    }
}