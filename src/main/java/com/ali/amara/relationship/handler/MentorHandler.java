package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.MentorDTO;
import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Component
public class MentorHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        if (!(details instanceof MentorDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for MENTOR relationship.");
        }
        MentorDTO dto = (MentorDTO) details;
        relationship.setMentorshipFocus(dto.getMentorshipFocus());
        relationship.setMentorshipDuration(dto.getMentorshipDuration());
        relationship.setMentorshipGoals(dto.getMentorshipGoals());
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        if (!(dto instanceof MentorDTO)) {
            return;
        }
        MentorDTO mentorDto = (MentorDTO) dto;
        mentorDto.setMentorshipFocus(relationship.getMentorshipFocus());
        mentorDto.setMentorshipDuration(relationship.getMentorshipDuration());
        mentorDto.setMentorshipGoals(relationship.getMentorshipGoals());
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.MENTOR;
    }
}