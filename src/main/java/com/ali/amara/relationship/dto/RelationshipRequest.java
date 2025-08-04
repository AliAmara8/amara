package com.ali.amara.relationship.dto;

import com.ali.amara.relationship.enums.RelationshipType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RelationshipRequest {
    @NotNull
    private Long followingId;
    @NotNull private RelationshipType type;
    // Tous les champs optionnels (expertiseArea, etc.)
    private String expertiseArea;
    // ...
}
