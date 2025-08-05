package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.NeighborDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class NeighborHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof NeighborDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for NEIGHBOR relationship.");
        }
        NeighborDTO dto = (NeighborDTO) details;
        relationship.setProximityDetails(dto.getProximityDetails());
        relationship.setSharedResources(dto.getSharedResources());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof NeighborDTO)) {
            return;
        }
        NeighborDTO neighborDto = (NeighborDTO) dto;
        neighborDto.setProximityDetails(relationship.getProximityDetails());
        neighborDto.setSharedResources(relationship.getSharedResources());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.NEIGHBOR;
    }
}