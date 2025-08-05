package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.SupplierDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class SupplierHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof SupplierDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for SUPPLIER relationship.");
        }
        SupplierDTO dto = (SupplierDTO) details;
        relationship.setSuppliedProducts(dto.getSuppliedProducts());
        relationship.setSupplyTerms(dto.getSupplyTerms());
        relationship.setDeliveryMethods(dto.getDeliveryMethods());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof SupplierDTO)) {
            return;
        }
        SupplierDTO supplierDto = (SupplierDTO) dto;
        supplierDto.setSuppliedProducts(relationship.getSuppliedProducts());
        supplierDto.setSupplyTerms(relationship.getSupplyTerms());
        supplierDto.setDeliveryMethods(relationship.getDeliveryMethods());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.SUPPLIER;
    }
}