package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.ResearchPartnerDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class ResearchPartnerHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof ResearchPartnerDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for RESEARCH_PARTNER relationship.");
        }
        ResearchPartnerDTO dto = (ResearchPartnerDTO) details;
        relationship.setResearchArea(dto.getResearchArea());
        relationship.setProjectDetails(dto.getProjectDetails());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof ResearchPartnerDTO)) {
            return;
        }
        ResearchPartnerDTO partnerDto = (ResearchPartnerDTO) dto;
        partnerDto.setResearchArea(relationship.getResearchArea());
        partnerDto.setProjectDetails(relationship.getProjectDetails());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.RESEARCH_PARTNER;
    }
}