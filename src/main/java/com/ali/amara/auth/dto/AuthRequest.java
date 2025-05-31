package com.ali.amara.auth.dto;

import com.ali.amara.auth.validation.annotations.EmailOrPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Login is required")
        @EmailOrPhone(message = "Login must be a valid email or phone number")
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
    // Méthode utilitaire pour déterminer le type de login
    public boolean isEmailLogin() {
        return login != null && login.contains("@");
    }

    public boolean isPhoneLogin() {
        return login != null && !login.contains("@") && login.matches("^\\+?[0-9]{10,15}$");
    }
}