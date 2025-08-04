package com.ali.amara.user;

import java.time.LocalDate;
import java.util.List;

public record UserProfileDTO(
    Long id,
    String username,
    String displayName,
    String profilePictureUrl,
    String coverPictureUrl,
    String biography,
    List<String> interests,
    String city,
    String region,
    LocalDate birthDate,
    String farmName
) {}
