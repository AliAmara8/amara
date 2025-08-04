package com.ali.amara.recommendation.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_user_id")
    private User recommendedUser;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String type;

    @Column(nullable = false)
    private Double score;

    private Double farmTypeScore;
    private Double locationScore;
    private Double cropsScore;
    private Double equipmentScore;
    private Double experienceScore;

    private Long targetId;
    private String targetType;

    @Builder.Default
    private Boolean isApplied = false;

    @Builder.Default
    private String status = "ACTIVE";

    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 1000)
    private String additionalInfo;

    private LocalDate createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }
}
