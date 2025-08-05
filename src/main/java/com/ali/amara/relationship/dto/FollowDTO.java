package com.ali.amara.relationship.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

// This DTO is for relationship types that have no extra fields.
@Data
@EqualsAndHashCode(callSuper = true)
public class FollowDTO extends RelationshipDTO {
    // No additional fields are needed for a simple FOLLOW.
}