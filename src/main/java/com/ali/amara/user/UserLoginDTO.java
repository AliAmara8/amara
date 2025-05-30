package com.ali.amara.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        @NotBlank String emailOrPhone,
        @NotBlank String password
) {}
