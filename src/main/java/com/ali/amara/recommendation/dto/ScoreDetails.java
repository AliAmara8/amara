package com.ali.amara.recommendation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoreDetails {
    private Double farmTypeScore;
    private Double locationScore;
    private Double cropsScore;
    private Double equipmentScore;
    private Double experienceScore;
}
