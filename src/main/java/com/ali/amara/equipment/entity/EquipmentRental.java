package com.ali.amara.equipment.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.equipment.enums.RentalStatus;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentRental extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", nullable = false)
    private User renter;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double dailyRate;

    @Column(nullable = false)
    private Double totalCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RentalStatus status = RentalStatus.PENDING;

    @Column
    private Integer hoursUsed;

    private String notes;
    private Boolean insuranceIncluded;
    private Double depositAmount;
    private LocalDate returnDate;
    private String condition;
    private String damageNotes;
    private Double additionalCharges;
    private String paymentStatus;
}
