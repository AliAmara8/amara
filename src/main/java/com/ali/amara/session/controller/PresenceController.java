package com.ali.amara.session.controller; // ou un autre package

import com.ali.amara.session.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final UserSessionService userSessionService;

    @MessageMapping("/heartbeat") // Le client enverra un message à /app/heartbeat
    public void handleHeartbeat(StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            userSessionService.updateHeartbeat(sessionId);
        }
    }
}