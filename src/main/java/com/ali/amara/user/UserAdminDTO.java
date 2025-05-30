package com.ali.amara.user;

import java.time.Instant;

public record UserAdminDTO(
        Long id,
        String email,
        String phone,
        String role,
        boolean isActive,
        Instant lastLogin,
        int loginAttempts
) {}
