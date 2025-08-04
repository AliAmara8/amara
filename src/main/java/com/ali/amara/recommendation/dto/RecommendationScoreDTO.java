package com.ali.amara.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationScoreDTO {
    private Long userId;
    private double farmTypeScore;     // Score basé sur le type d'exploitation
    private double locationScore;     // Score basé sur la proximité géographique
    private double cropsScore;        // Score basé sur les cultures communes
    private double equipmentScore;    // Score basé sur les équipements partagés
    private boolean isVerified;       // Si le profil est vérifié
    private boolean isActive;         // Si l'utilisateur est actif récemment

    // Calcul du score total pondéré
    public double calculateTotalScore(double farmTypeWeight,
                                      double locationWeight,
                                      double cropsWeight,
                                      double equipmentWeight) {
        double baseScore = (farmTypeScore * farmTypeWeight) +
                (locationScore * locationWeight) +
                (cropsScore * cropsWeight) +
                (equipmentScore * equipmentWeight);

        // Bonus pour les profils vérifiés et actifs
        double verifiedBonus = isVerified ? 0.1 : 0;
        double activeBonus = isActive ? 0.05 : 0;

        return baseScore + verifiedBonus + activeBonus;
    }

    // Méthode utilitaire pour obtenir le score total avec les pondérations par défaut
    public double getTotalScore() {
        return calculateTotalScore(0.30, 0.30, 0.25, 0.15);
    }
}
