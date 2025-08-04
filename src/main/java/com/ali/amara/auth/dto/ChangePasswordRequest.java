package com.ali.amara.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête pour changer le mot de passe")
public record ChangePasswordRequest(

        @Schema(description = "Mot de passe actuel", example = "currentPassword123!")
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @Schema(description = "Nouveau mot de passe", example = "newPassword123!")
        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
        )
        String newPassword,

        @Schema(description = "Confirmation du nouveau mot de passe", example = "newPassword123!")
        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) {
    public ChangePasswordRequest {
        // Validation personnalisée pour s'assurer que les mots de passe correspondent
        if (newPassword != null && !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirmation password do not match");
        }
    }
}