package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResearchPartnerDTO extends RelationshipDTO {

    @NotBlank(message = "Research area cannot be blank")
    private String researchArea;

    private String projectDetails;
}