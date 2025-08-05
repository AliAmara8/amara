package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.BuyerDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class BuyerHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof BuyerDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for BUYER relationship.");
        }
        BuyerDTO dto = (BuyerDTO) details;
        relationship.setPreferredProducts(dto.getPreferredProducts());
        relationship.setPurchaseTerms(dto.getPurchaseTerms());
        relationship.setPaymentMethods(dto.getPaymentMethods());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof BuyerDTO)) {
            return;
        }
        BuyerDTO buyerDto = (BuyerDTO) dto;
        buyerDto.setPreferredProducts(relationship.getPreferredProducts());
        buyerDto.setPurchaseTerms(relationship.getPurchaseTerms());
        buyerDto.setPaymentMethods(relationship.getPaymentMethods());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.BUYER;
    }
}