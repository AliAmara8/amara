package com.ali.amara.recommendation.controller;

import com.ali.amara.recommendation.dto.RecommendationResponse;
import com.ali.amara.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.generateRecommendations(userId));
    }

    @GetMapping("/users/{userId}/similar-farmers")
    public ResponseEntity<List<RecommendationResponse>> getSimilarFarmers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getSimilarFarmers(userId, limit));
    }

    @GetMapping("/users/{userId}/by-location")
    public ResponseEntity<List<RecommendationResponse>> getByLocation(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getByLocation(userId, limit));
    }

    @GetMapping("/users/{userId}/by-farm-type")
    public ResponseEntity<List<RecommendationResponse>> getByFarmType(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getByFarmType(userId, limit));
    }
}

