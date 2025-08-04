package com.ali.amara.security.controller;

import com.ali.amara.auth.exception.InvalidTokenException;
import com.ali.amara.auth.service.JwtService;
import com.ali.amara.session.service.UserSessionService;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class SecurityController {

    private final UserSessionService userSessionService;
    private final JwtService jwtService;
    private final UserRepository userRepository; // Injecter pour trouver l'utilisateur

    @PostMapping("/emergency-logout")
    @Operation(summary = "Déconnecte toutes les sessions d'un utilisateur via un token d'urgence")
    public ResponseEntity<Void> emergencyLogout(@RequestParam("token") String token) {

        // 1. On valide le token avec la nouvelle méthode du service
        if (!jwtService.isEmergencyLogoutTokenValid(token)) {
            throw new InvalidTokenException("Invalid or expired emergency token");
        }

        // 2. Si le token est valide, on peut extraire l'email en toute sécurité
        final String email = jwtService.extractUsername(token);

        // 3. Trouver l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 4. Invalider toutes ses sessions et tokens
        userSessionService.invalidateAllSessionsForUser(user.getId());

        // 5. Mettre le token en blacklist (si vous avez ce service)
        // tokenBlacklistService.blacklist(token);

        return ResponseEntity.ok().build();
    }
}
