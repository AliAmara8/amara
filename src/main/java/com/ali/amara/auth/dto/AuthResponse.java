package com.ali.amara.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String token,
        String tokenType,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant expiresAt,

        Long expiresIn, // Durée en secondes

        UserResponse user,

        String message // Pour les messages de confirmation (inscription, etc.)
) {
    // Constructeur par défaut avec Bearer token
    public AuthResponse(String token, Instant expiresAt, UserResponse user) {
        this(token, "Bearer", expiresAt, null, user, null);
    }

    // Constructeur avec durée d'expiration
    public AuthResponse(String token, Instant expiresAt, Long expiresIn, UserResponse user) {
        this(token, "Bearer", expiresAt, expiresIn, user, null);
    }

    // Constructeur pour les réponses avec message seulement
    public static AuthResponse withMessage(String message) {
        return new AuthResponse(null, null, null, null, null, message);
    }

    // Constructeur pour les réponses avec message et utilisateur
    public static AuthResponse withMessageAndUser(String message, UserResponse user) {
        return new AuthResponse(null, null, null, null, user, message);
    }
}