package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.FarmingPartnerDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class FarmingPartnerHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof FarmingPartnerDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for FARMING_PARTNER relationship.");
        }
        FarmingPartnerDTO dto = (FarmingPartnerDTO) details;
        relationship.setCropTypes(dto.getCropTypes());
        relationship.setFarmingActivities(dto.getFarmingActivities());
        relationship.setCollaborationDetails(dto.getCollaborationDetails());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof FarmingPartnerDTO)) {
            return;
        }
        FarmingPartnerDTO partnerDto = (FarmingPartnerDTO) dto;
        partnerDto.setCropTypes(relationship.getCropTypes());
        partnerDto.setFarmingActivities(relationship.getFarmingActivities());
        partnerDto.setCollaborationDetails(relationship.getCollaborationDetails());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.FARMING_PARTNER;
    }
}