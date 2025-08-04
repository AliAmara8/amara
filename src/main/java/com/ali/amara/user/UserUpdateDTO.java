package com.ali.amara.user;

import java.time.LocalDate;
import java.util.List;

public record UserUpdateDTO(
    String username,
    String displayName,
    String biography,
    List<String> interests,
    String city,
    String region,
    LocalDate birthDate,
    String farmName
) {}
