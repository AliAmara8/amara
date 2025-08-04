package com.ali.amara.equipment.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.equipment.enums.EquipmentType;
import com.ali.amara.equipment.enums.EquipmentStatus;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    private String brand;
    private String model;
    private Integer yearOfManufacture;

    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;

    @Column(nullable = false)
    private Double purchasePrice;

    @Column(nullable = false)
    private String currency;

    private Double currentValue;

    @Column(length = 1000)
    private String technicalSpecifications;

    private String description;

    @Builder.Default
    private boolean isAvailable = true;

    @Builder.Default
    private Double hoursOfUse = 0.0;

    private Integer maintenanceInterval;

    private Double rentalRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.OPERATIONAL;

    @ElementCollection
    @CollectionTable(name = "equipment_maintenance_history",
            joinColumns = @JoinColumn(name = "equipment_id"))
    @Builder.Default
    private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    public void scheduleNextMaintenance() {
        LocalDate lastMaintenance = maintenanceHistory.isEmpty() ?
            purchaseDate :
            maintenanceHistory.get(maintenanceHistory.size() - 1).getMaintenanceDate();

        nextMaintenanceDate = lastMaintenance.plusMonths(3);  // Par défaut tous les 3 mois
    }

    public boolean needsMaintenance() {
        if (maintenanceInterval != null && hoursOfUse >= maintenanceInterval.doubleValue()) {
            return true;
        }
        return nextMaintenanceDate != null && LocalDate.now().isAfter(nextMaintenanceDate);
    }

    public void addMaintenanceRecord(MaintenanceRecord record) {
        maintenanceHistory.add(record);
        hoursOfUse = 0.0;  // Réinitialisation des heures après maintenance
        lastMaintenanceDate = record.getMaintenanceDate();
        scheduleNextMaintenance();
    }

    public double calculateDepreciation() {
        if (purchaseDate == null || purchasePrice == null) {
            return 0.0;
        }

        int ageInYears = LocalDate.now().getYear() - purchaseDate.getYear();
        double annualDepreciation = purchasePrice * 0.1; // 10% par an
        currentValue = purchasePrice - (annualDepreciation * ageInYears);
        return currentValue;
    }
}
