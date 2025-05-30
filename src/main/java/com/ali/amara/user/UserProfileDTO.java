package com.ali.amara.user;

import java.time.LocalDate;

public record UserProfileDTO(
        Long id,
        String username,
        String displayName, // firstName + lastName
        String profilePictureUrl,
        String coverPictureUrl,
        String profession,
        String city,
        LocalDate birthDate
) {}
