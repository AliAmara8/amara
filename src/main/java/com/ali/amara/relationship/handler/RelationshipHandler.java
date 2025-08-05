package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;

public interface RelationshipHandler {
    /**
     * Sets type-specific properties on the Relationship entity from a DTO.
     */
    void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details);

    /**
     * Maps type-specific properties from the Relationship entity to a DTO.
     */
    void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship);

    /**
     * Returns the RelationshipType that this handler is responsible for.
     */
    RelationshipType getHandledType();
}