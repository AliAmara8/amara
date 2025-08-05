package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FarmingPartnerDTO extends RelationshipDTO {

    @NotBlank(message = "Crop types cannot be blank")
    private String cropTypes;

    private String farmingActivities;
    private String collaborationDetails;
}