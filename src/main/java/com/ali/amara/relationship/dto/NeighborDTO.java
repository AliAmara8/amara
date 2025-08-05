package com.ali.amara.relationship.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NeighborDTO extends RelationshipDTO {

    private String proximityDetails;
    private String sharedResources;
}