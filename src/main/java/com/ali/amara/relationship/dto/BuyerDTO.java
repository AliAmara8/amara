package com.ali.amara.relationship.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BuyerDTO extends RelationshipDTO {

    private String preferredProducts;
    private String purchaseTerms;
    private String paymentMethods;
}