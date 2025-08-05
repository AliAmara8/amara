package com.ali.amara.relationship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MentorDTO extends RelationshipDTO {

    @NotBlank(message = "Mentorship focus cannot be blank")
    private String mentorshipFocus;

    private String mentorshipDuration;
    private String mentorshipGoals;
}