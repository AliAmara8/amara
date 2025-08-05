package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.ExpertConnectDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class ExpertConnectHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof ExpertConnectDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for EXPERT_CONNECT");
        }
        ExpertConnectDTO expertDto = (ExpertConnectDTO) details;
        relationship.setExpertiseArea(expertDto.getExpertiseArea());
        relationship.setCertification(expertDto.getCertification());
        relationship.setCertificationExpiry(expertDto.getCertificationExpiry());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof ExpertConnectDTO)) return;
        ExpertConnectDTO expertDto = (ExpertConnectDTO) dto;
        expertDto.setExpertiseArea(relationship.getExpertiseArea());
        expertDto.setCertification(relationship.getCertification());
        expertDto.setCertificationExpiry(relationship.getCertificationExpiry());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.EXPERT_CONNECT;
    }
}