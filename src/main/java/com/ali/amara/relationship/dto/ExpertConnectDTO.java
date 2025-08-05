package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExpertConnectDTO extends RelationshipDTO {

    @NotBlank(message = "Expertise area cannot be blank")
    private String expertiseArea;

    private String certification;

    @Future(message = "Certification expiry date must be in the future")
    private LocalDateTime certificationExpiry;
}