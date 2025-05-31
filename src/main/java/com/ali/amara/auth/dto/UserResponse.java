package com.ali.amara.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Set<String> roles,
        boolean isEnabled,
        boolean isAccountNonExpired,
        boolean isAccountNonLocked,
        boolean isCredentialsNonExpired,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime lastLoginAt,

        String profilePictureUrl
) {
    // Constructeur de convenance pour les cas simples
    public UserResponse(Long id, String firstName, String lastName, String email) {
        this(id, firstName, lastName, email, null, null, true, true, true, true, null, null, null);
    }

    // Méthode utilitaire pour masquer les informations sensibles
    public UserResponse withMaskedSensitiveInfo() {
        return new UserResponse(
                id,
                firstName,
                lastName,
                maskEmail(email),
                maskPhone(phone),
                roles,
                isEnabled,
                isAccountNonExpired,
                isAccountNonLocked,
                isCredentialsNonExpired,
                createdAt,
                null, // On ne révèle pas la dernière connexion
                profilePictureUrl
        );
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) return email;
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() <= 4) return phone;
        return "***" + phone.substring(phone.length() - 4);
    }
}