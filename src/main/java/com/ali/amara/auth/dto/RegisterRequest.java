package com.ali.amara.auth.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Length(min = 2, max = 50, message = "First name must be between 2-50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Length(min = 2, max = 50, message = "Last name must be between 2-50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "Password must be 8+ chars with letter, number and special char"
        )
        String password,

        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone must be 10-15 digits with optional + prefix"
        )
        String phone
) {
    public boolean isPasswordValid() {
        return password != null && password.length() >= 8;
    }
}