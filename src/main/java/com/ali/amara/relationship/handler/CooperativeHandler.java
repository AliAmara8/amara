package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.CooperativeDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class CooperativeHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof CooperativeDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for COOPERATIVE relationship.");
        }
        CooperativeDTO dto = (CooperativeDTO) details;
        relationship.setMembershipDetails(dto.getMembershipDetails());
        relationship.setCooperativeRole(dto.getCooperativeRole());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof CooperativeDTO)) {
            return;
        }
        CooperativeDTO cooperativeDto = (CooperativeDTO) dto;
        cooperativeDto.setMembershipDetails(relationship.getMembershipDetails());
        cooperativeDto.setCooperativeRole(relationship.getCooperativeRole());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.COOPERATIVE;
    }
}