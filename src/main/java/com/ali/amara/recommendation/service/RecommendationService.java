package com.ali.amara.recommendation.service;

import com.ali.amara.equipment.entity.Equipment;
import com.ali.amara.equipment.enums.EquipmentType;
import com.ali.amara.farm.entity.FarmType;
import com.ali.amara.notification.service.NotificationService;
import com.ali.amara.recommendation.dto.RecommendationResponse;
import com.ali.amara.recommendation.entity.Recommendation;
import com.ali.amara.recommendation.enums.RecommendationType;
import com.ali.amara.recommendation.repository.RecommendationRepository;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final double LOCATION_WEIGHT = 0.3;
    private static final double FARM_TYPE_WEIGHT = 0.3;
    private static final double CROPS_WEIGHT = 0.2;
    private static final double EQUIPMENT_WEIGHT = 0.15;
    private static final double EXPERIENCE_WEIGHT = 0.05;
    private static final double HIGH_SIMILARITY_THRESHOLD = 0.8;

    @Transactional
    public List<RecommendationResponse> generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get existing recommendation IDs
        List<Long> existingRecommendations = recommendationRepository
                .findExistingRecommendations(userId);

        // Find potential new users
        List<User> potentialUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .filter(u -> !existingRecommendations.contains(u.getId()))
                .toList();

        // Calculate scores and create recommendations
        List<Recommendation> newRecommendations = potentialUsers.stream()
                .map(potentialUser -> calculateRecommendation(user, potentialUser))
                .filter(rec -> rec.getScore() >= 0.5) // Minimum relevance threshold
                .toList();

        // Save new recommendations
        recommendationRepository.saveAll(newRecommendations);

        // Send notifications for new recommendations
        newRecommendations.forEach(recommendation -> {
            User recommendedUser = userRepository.findById(recommendation.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            notificationService.sendNewRecommendationNotification(
                    user.getId(),
                    recommendedUser.getUsername()
            );

            // Send special notification for high similarity matches
            if (recommendation.getScore() >= HIGH_SIMILARITY_THRESHOLD) {
                notificationService.sendHighSimilarityMatchNotification(
                        user.getId(),
                        recommendedUser.getUsername(),
                        recommendation.getScore()
                );
            }
        });

        // Convert and return results
        return newRecommendations.stream()
                .map(this::toRecommendationResponse)
                .sorted(Comparator.comparing(RecommendationResponse::getScore).reversed())
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getSimilarFarmers(Long userId, int limit) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recommendationRepository.findSimilarFarmers(userId).stream()
                .map(this::toRecommendationResponse)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getByLocation(Long userId, int limit) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recommendationRepository.findByLocationScore(userId).stream()
                .map(this::toRecommendationResponse)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getByFarmType(Long userId, int limit) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recommendationRepository.findByFarmTypeScore(userId).stream()
                .map(this::toRecommendationResponse)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Rafraîchit les recommandations pour un utilisateur donné
     * À exécuter périodiquement ou après des mises à jour importantes
     */
    @Transactional
    public void refreshRecommendations(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Deactivate old recommendations
        List<Recommendation> oldRecommendations = recommendationRepository.findActiveRecommendations(userId);
        oldRecommendations.forEach(rec -> rec.setIsActive(false));
        recommendationRepository.saveAll(oldRecommendations);

        // Generate new recommendations
        generateRecommendations(userId);
    }

    /**
     * Rafraîchit automatiquement les recommandations pour tous les utilisateurs
     * S'exécute tous les jours à 3h du matin
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void autoRefreshAllRecommendations() {
        // Récupérer tous les utilisateurs actifs
        List<User> activeUsers = userRepository.findByEnabledTrue();

        for (User user : activeUsers) {
            try {
                // Désactiver les anciennes recommandations
                List<Recommendation> oldRecommendations = recommendationRepository.findActiveRecommendations(user.getId());
                oldRecommendations.forEach(rec -> rec.setIsActive(false));
                recommendationRepository.saveAll(oldRecommendations);

                // Générer de nouvelles recommandations
                List<RecommendationResponse> newRecommendations = generateRecommendations(user.getId());

                // Notifier l'utilisateur s'il y a de nouvelles recommandations intéressantes
                notifyUserOfHighQualityMatches(user, newRecommendations);
            } catch (Exception e) {
                // Logger l'erreur mais continuer avec les autres utilisateurs
                log.error("Erreur lors du rafraîchissement des recommandations pour l'utilisateur {}: {}",
                    user.getId(), e.getMessage());
            }
        }
    }

    /**
     * Rafraîchit les recommandations pour les utilisateurs récemment actifs
     * S'exécute toutes les 4 heures
     */
    @Scheduled(cron = "0 0 */4 * * *")
    @Transactional
    public void refreshRecommendationsForActiveUsers() {
        // Récupérer les utilisateurs actifs dans les dernières 24 heures
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<User> recentlyActiveUsers = userRepository.findByLastActivityAfter(oneDayAgo);

        for (User user : recentlyActiveUsers) {
            try {
                refreshRecommendations(user.getId());
            } catch (Exception e) {
                log.error("Erreur lors du rafraîchissement des recommandations pour l'utilisateur actif {}: {}",
                    user.getId(), e.getMessage());
            }
        }
    }

    private void notifyUserOfHighQualityMatches(User user, List<RecommendationResponse> newRecommendations) {
        List<RecommendationResponse> highQualityMatches = newRecommendations.stream()
            .filter(rec -> rec.getScore() >= HIGH_SIMILARITY_THRESHOLD)
            .toList();

        if (!highQualityMatches.isEmpty()) {
            if (highQualityMatches.size() > 1) {
                notificationService.sendNewRecommendationNotification(
                    user.getId(),
                    String.format("Nous avons trouvé %d nouveaux agriculteurs qui pourraient vous intéresser!",
                        highQualityMatches.size())
                );
            } else {
                RecommendationResponse match = highQualityMatches.get(0);
                notificationService.sendHighSimilarityMatchNotification(
                    user.getId(),
                    match.getUsername(),
                    match.getScore()
                );
            }
        }
    }

    private Recommendation calculateRecommendation(User user, User potentialMatch) {
        double farmTypeScore = calculateFarmTypeScore(user, potentialMatch);
        double locationScore = calculateLocationScore(user, potentialMatch);
        double cropsScore = calculateCropSimilarity(user, potentialMatch);
        double equipmentScore = calculateEquipmentSimilarity(user, potentialMatch);
        double experienceScore = calculateExperienceScore(user, potentialMatch);

        double totalScore = (farmTypeScore * FARM_TYPE_WEIGHT) +
                (locationScore * LOCATION_WEIGHT) +
                (cropsScore * CROPS_WEIGHT) +
                (equipmentScore * EQUIPMENT_WEIGHT) +
                (experienceScore * EXPERIENCE_WEIGHT);

        String recommendationType = determineRecommendationType(farmTypeScore, locationScore, cropsScore).toString();

        return Recommendation.builder()
                .userId(user.getId())
                .recommendedUser(potentialMatch)  // L'entité User complète
                .type(recommendationType)
                .score(totalScore)
                .farmTypeScore(farmTypeScore)
                .locationScore(locationScore)
                .cropsScore(cropsScore)
                .equipmentScore(equipmentScore)
                .experienceScore(experienceScore)
                .isActive(true)
                .title("Recommandation de partenariat")
                .description(String.format("Un agriculteur avec %.0f%% de compatibilité avec votre profil", totalScore * 100))
                .status("ACTIVE")
                .build();
    }

    private RecommendationType determineRecommendationType(double farmTypeScore,
                                                           double locationScore, double cropsScore) {
        if (farmTypeScore > 0.8) return RecommendationType.FARM_TYPE;
        if (locationScore > 0.8) return RecommendationType.LOCATION;
        if (cropsScore > 0.8) return RecommendationType.CROPS;
        return RecommendationType.MIXED;
    }

    private double calculateFarmTypeScore(User user1, User user2) {
        if (user1.getFarm() == null || user2.getFarm() == null) {
            return 0.0;
        }

        FarmType type1 = user1.getFarm().getFarmType();
        FarmType type2 = user2.getFarm().getFarmType();

        if (type1 == null || type2 == null) {
            return 0.0;
        }

        // Score parfait si même type de ferme
        if (type1 == type2) {
            return 1.0;
        }

        // Score partiel pour les types compatibles
        if (isCompatibleFarmType(type1, type2)) {
            return 0.5;
        }

        return 0.1;
    }

    private boolean isCompatibleFarmType(FarmType type1, FarmType type2) {
        if (type1 == null || type2 == null) {
            return false;
        }

        // L'agriculture mixte est compatible avec tous les types
        if (type1 == FarmType.MIXED_FARM || type2 == FarmType.MIXED_FARM) {
            return true;
        }

        // Types de base et leurs compatibilités
        if (type1 == FarmType.CROP_FARM) {
            return type2 == FarmType.ORGANIC_FARM ||
                   type2 == FarmType.GREENHOUSE ||
                   type2 == FarmType.PERMACULTURE ||
                   type2 == FarmType.PLANTATION;
        }

        if (type1 == FarmType.ORGANIC_FARM) {
            return type2 == FarmType.CROP_FARM ||
                   type2 == FarmType.PERMACULTURE ||
                   type2 == FarmType.PLANTATION;
        }

        if (type1 == FarmType.LIVESTOCK_FARM) {
            return type2 == FarmType.AQUAPONICS;
        }

        if (type1 == FarmType.GREENHOUSE) {
            return type2 == FarmType.CROP_FARM ||
                   type2 == FarmType.HYDROPONICS ||
                   type2 == FarmType.PLANTATION;
        }

        if (type1 == FarmType.HYDROPONICS) {
            return type2 == FarmType.GREENHOUSE ||
                   type2 == FarmType.AQUAPONICS;
        }

        if (type1 == FarmType.PERMACULTURE) {
            return type2 == FarmType.ORGANIC_FARM ||
                   type2 == FarmType.CROP_FARM;
        }

        if (type1 == FarmType.AQUAPONICS) {
            return type2 == FarmType.HYDROPONICS ||
                   type2 == FarmType.LIVESTOCK_FARM;
        }

        if (type1 == FarmType.PLANTATION) {
            return type2 == FarmType.CROP_FARM ||
                   type2 == FarmType.ORGANIC_FARM ||
                   type2 == FarmType.GREENHOUSE;
        }

        // Par défaut (OTHER ou cas non gérés)
        return false;
    }

    private double calculateLocationScore(User user, User other) {
        if (user.getCity() == null || other.getCity() == null) return 0.0;

        boolean sameCity = Objects.equals(user.getCity(), other.getCity());
        boolean sameRegion = Objects.equals(user.getRegion(), other.getRegion());

        if (sameCity) return 1.0;
        if (sameRegion) return 0.7;
        return 0.0;
    }

    private double calculateCropSimilarity(User user1, User user2) {
        if (user1.getCrops().isEmpty() || user2.getCrops().isEmpty()) {
            return 0.0;
        }

        Set<String> crops1 = user1.getCrops();
        Set<String> crops2 = user2.getCrops();

        // Calcul de l'indice de Jaccard
        Set<String> intersection = new HashSet<>(crops1);
        intersection.retainAll(crops2);
        Set<String> union = new HashSet<>(crops1);
        union.addAll(crops2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private double calculateEquipmentSimilarity(User user1, User user2) {
        Set<EquipmentType> equipment1 = user1.getEquipment().stream()
                .map(Equipment::getType)
                .collect(Collectors.toSet());
        Set<EquipmentType> equipment2 = user2.getEquipment().stream()
                .map(Equipment::getType)
                .collect(Collectors.toSet());

        if (equipment1.isEmpty() || equipment2.isEmpty()) {
            return 0.0;
        }

        // Calcul de l'indice de Jaccard pour les types d'équipement
        Set<EquipmentType> intersection = new HashSet<>(equipment1);
        intersection.retainAll(equipment2);
        Set<EquipmentType> union = new HashSet<>(equipment1);
        union.addAll(equipment2);

        return (double) intersection.size() / union.size();
    }

    private double calculateExperienceScore(User user1, User user2) {
        if (user1.getFarm() == null || user2.getFarm() == null ||
            user1.getFarm().getYearsOfExperience() == null || user2.getFarm().getYearsOfExperience() == null) {
            return 0.0;
        }

        int diff = Math.abs(user1.getFarm().getYearsOfExperience() - user2.getFarm().getYearsOfExperience());
        if (diff <= 2) return 1.0;     // Très similaire
        if (diff <= 5) return 0.7;     // Assez similaire
        if (diff <= 10) return 0.4;    // Modérément similaire
        return 0.1;                    // Peu similaire
    }

    private RecommendationResponse toRecommendationResponse(Recommendation recommendation) {
        User recommendedUser = recommendation.getRecommendedUser();

        RecommendationResponse.ScoreDetails scoreDetails = RecommendationResponse.ScoreDetails.builder()
                .farmTypeScore(recommendation.getFarmTypeScore())
                .locationScore(recommendation.getLocationScore())
                .cropsScore(recommendation.getCropsScore())
                .equipmentScore(recommendation.getEquipmentScore())
                .experienceScore(recommendation.getExperienceScore())
                .build();

        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .recommendedUserId(recommendedUser.getId())
                .username(recommendedUser.getUsername())
                .title(recommendation.getTitle())
                .description(recommendation.getDescription())
                .type(recommendation.getType())
                .score(recommendation.getScore())
                .scores(scoreDetails)
                .targetId(recommendation.getTargetId())
                .targetType(recommendation.getTargetType())
                .isApplied(recommendation.getIsApplied())
                .status(recommendation.getStatus())
                .additionalInfo(recommendation.getAdditionalInfo())
                .build();
    }
}
