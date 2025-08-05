package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierDTO extends RelationshipDTO {

    @NotBlank(message = "Supplied products cannot be blank")
    private String suppliedProducts;

    private String supplyTerms;
    private String deliveryMethods;
}