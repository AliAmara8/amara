package com.ali.amara.recommendation.dto;

import com.ali.amara.farm.enums.FarmType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
    private Long id;
    private Long recommendedUserId;
    private String username;
    private String title;
    private String description;
    private String type;
    private FarmType farmType;
    private String city;
    private String region;
    private Double score;
    private ScoreDetails scores;
    private Long targetId;
    private String targetType;
    private Boolean isApplied;
    private String status;
    private String additionalInfo;

    @Data
    @Builder
    public static class ScoreDetails {
        private Double farmTypeScore;
        private Double locationScore;
        private Double cropsScore;
        private Double equipmentScore;
        private Double experienceScore;
    }
}
