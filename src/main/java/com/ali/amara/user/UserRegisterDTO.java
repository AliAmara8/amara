package com.ali.amara.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(
        @NotBlank @Size(min = 2, max = 50) String firstName,
        @NotBlank @Size(min = 2, max = 50) String lastName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$") String password,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone
) {}
