package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.EquipmentShareDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class EquipmentShareHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof EquipmentShareDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for EQUIPMENT_SHARE relationship.");
        }
        EquipmentShareDTO dto = (EquipmentShareDTO) details;
        relationship.setEquipmentTypes(dto.getEquipmentTypes());
        relationship.setSharingTerms(dto.getSharingTerms());
        relationship.setLocationDetails(dto.getLocationDetails());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof EquipmentShareDTO)) {
            return;
        }
        EquipmentShareDTO equipmentDto = (EquipmentShareDTO) dto;
        equipmentDto.setEquipmentTypes(relationship.getEquipmentTypes());
        equipmentDto.setSharingTerms(relationship.getSharingTerms());
        equipmentDto.setLocationDetails(relationship.getLocationDetails());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.EQUIPMENT_SHARE;
    }
}