package com.ali.amara.equipment.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.equipment.enums.ReservationStatus;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentReservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    private String purpose;
    private String notes;
    @Builder.Default
    private Boolean isConfirmed = false;
    private LocalDate confirmationDate;
    private String cancellationReason;
    private LocalDate cancellationDate;
    private String rejectionReason;
    private LocalDate rejectionDate;

    @Column(nullable = false)
    private Double estimatedCost;

    @Builder.Default
    private Boolean needsDelivery = false;
    private String deliveryAddress;

    @Column(length = 1000)
    private String terms;

    @Column(nullable = false)
    private Double deposit;
}
