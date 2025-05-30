package com.ali.amara.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 2, max = 50) String firstName,
        @Size(min = 2, max = 50) String lastName,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String profilePictureUrl,
        String biography
) {}
