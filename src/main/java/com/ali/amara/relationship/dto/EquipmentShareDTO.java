package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentShareDTO extends RelationshipDTO {

    @NotBlank(message = "Equipment types cannot be blank")
    private String equipmentTypes;

    @NotBlank(message = "Sharing terms must be specified")
    private String sharingTerms;

    private String locationDetails;
}