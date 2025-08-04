package com.ali.amara.recommendation.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RecommendationCriteria {
    // Poids des différents critères (total = 1.0)
    @Builder.Default
    private double farmTypeWeight = 0.30;    // Type d'exploitation
    @Builder.Default
    private double locationWeight = 0.30;    // Localisation
    @Builder.Default
    private double cropsWeight = 0.25;       // Cultures
    @Builder.Default
    private double equipmentWeight = 0.15;   // Équipement

    // Critères de filtrage
    private String region;                   // Filtrer par région
    private Integer maxDistance;             // Distance maximale en km
    private String farmType;                // Type spécifique d'exploitation
    private Integer minCropsInCommon;       // Nombre minimum de cultures en commun

    // Critères de tri
    @Builder.Default
    private boolean prioritizeVerified = true;    // Priorité aux profils vérifiés
    @Builder.Default
    private boolean prioritizeActive = true;      // Priorité aux utilisateurs actifs

    // Limites
    @Builder.Default
    private int maxResults = 10;                  // Nombre maximum de résultats

    public static RecommendationCriteria getDefault() {
        return RecommendationCriteria.builder()
                .farmTypeWeight(0.30)
                .locationWeight(0.30)
                .cropsWeight(0.25)
                .equipmentWeight(0.15)
                .prioritizeVerified(true)
                .prioritizeActive(true)
                .maxResults(10)
                .build();
    }
}
