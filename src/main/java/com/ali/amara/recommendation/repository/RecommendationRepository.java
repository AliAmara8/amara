package com.ali.amara.recommendation.repository;

import com.ali.amara.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);

    @Query("SELECT r FROM Recommendation r WHERE r.targetId = :targetId AND r.targetType = :targetType")
    List<Recommendation> findByTargetIdAndType(Long targetId, String targetType);

    @Query("SELECT r FROM Recommendation r WHERE r.userId = :userId AND r.createdDate >= :date")
    List<Recommendation> findRecentByUserId(Long userId, LocalDate date);

    @Query("SELECT r FROM Recommendation r WHERE r.score >= :minScore AND r.status = 'ACTIVE'")
    List<Recommendation> findHighPriorityRecommendations(Double minScore);

    @Query("SELECT r.recommendedUser.id FROM Recommendation r WHERE r.userId = :userId AND r.status = 'ACTIVE'")
    List<Long> findExistingRecommendations(Long userId);

    @Query("SELECT r FROM Recommendation r WHERE r.userId = :userId AND r.status = 'ACTIVE' AND r.isActive = true")
    List<Recommendation> findActiveRecommendations(Long userId);

    @Query("""
            SELECT r FROM Recommendation r 
            WHERE r.farmTypeScore >= 0.7 
            AND r.userId = :userId 
            AND r.status = 'ACTIVE'
            AND r.isActive = true
            ORDER BY r.score DESC
            """)
    List<Recommendation> findSimilarFarmers(Long userId);

    @Query("""
            SELECT r FROM Recommendation r 
            WHERE r.locationScore >= 0.8 
            AND r.userId = :userId 
            AND r.status = 'ACTIVE'
            AND r.isActive = true
            ORDER BY r.locationScore DESC
            """)
    List<Recommendation> findByLocationScore(Long userId);

    @Query("""
            SELECT r FROM Recommendation r 
            WHERE r.farmTypeScore >= 0.8 
            AND r.userId = :userId 
            AND r.status = 'ACTIVE'
            AND r.isActive = true
            ORDER BY r.farmTypeScore DESC
            """)
    List<Recommendation> findByFarmTypeScore(Long userId);
}
