package com.ali.amara.recommendation.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "smart_recommendations")
@Getter
@Setter
public class SmartRecommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(nullable = false)
    private String recommendationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    private String priority; // HIGH, MEDIUM, LOW
    private LocalDateTime validUntil;
    private Boolean isAcknowledged;
    private LocalDateTime acknowledgedAt;

    // Weather-based data
    private String weatherCondition;
    private Double temperature;
    private Double rainfall;
    private Double humidity;
    private String weatherForecast;
    private String weatherImpact;

    // Crop-specific data
    private String cropType;
    private String growthStage;
    private String expectedImpact;
    private String preventiveActions;
    private String remedialActions;

    // Market-based data
    private String marketCondition;
    private String priceMovement;
    private String tradingAdvice;
    private String marketOpportunities;

    // Resource optimization
    private String resourceType; // water, fertilizer, pesticide, etc.
    private String optimizationAdvice;
    private String expectedSavings;
    private String implementationSteps;

    @ElementCollection
    @CollectionTable(name = "recommendation_actions")
    private List<Action> suggestedActions = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    public static class Action {
        private String actionType;
        private String description;
        private String timing;
        private String resources;
        private String expectedOutcome;
        private Boolean isCompleted;
        private LocalDateTime completedAt;
    }

    // AI insights
    private String aiAnalysis;
    private Double confidenceScore;
    private String dataSources;
    private String alternativeOptions;
    private String riskAssessment;
    private String costBenefitAnalysis;

    // Feedback and tracking
    private Boolean wasHelpful;
    private String farmerFeedback;
    private String actualOutcome;
    private String lessonsLearned;
}