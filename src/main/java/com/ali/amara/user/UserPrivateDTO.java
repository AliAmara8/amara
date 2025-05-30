package com.ali.amara.user;

import java.time.Instant;

public record UserPrivateDTO(
        Long id,
        String email,
        String phone,
        String role,
        boolean isActive,
        Instant createdAt
) {}
