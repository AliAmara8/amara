package com.ali.amara.websocket;

import com.ali.amara.session.service.UserSessionService;
import com.ali.amara.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserSessionService userSessionService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // Spring Security place l'utilisateur authentifié dans les headers de la session STOMP
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) headers.getUser();

        if (userToken != null) {
            User currentUser = (User) userToken.getPrincipal();
            String sessionId = headers.getSessionId();

            userSessionService.userConnectedViaWebSocket(currentUser, sessionId);
            log.info("STOMP Client connected: userId={}, sessionId={}", currentUser.getId(), sessionId);
        } else {
            log.warn("STOMP Client connected without authentication principal.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        userSessionService.userDisconnectedViaWebSocket(sessionId);
        log.info("STOMP Client disconnected: sessionId={}", sessionId);
    }
}
