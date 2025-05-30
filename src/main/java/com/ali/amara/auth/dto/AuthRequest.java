package com.ali.amara.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @EmailOrPhone String login,
        @NotBlank @Size(min = 8) String password
) {}
