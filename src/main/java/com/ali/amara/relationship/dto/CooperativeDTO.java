package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CooperativeDTO extends RelationshipDTO {

    private String membershipDetails;

    @NotBlank(message = "Cooperative role must be specified")
    private String cooperativeRole;
}