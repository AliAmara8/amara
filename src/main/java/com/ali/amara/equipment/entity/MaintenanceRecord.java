package com.ali.amara.equipment.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRecord {
    private LocalDate maintenanceDate;
    private String description;
    private String technician;
    private Double cost;
    private String type;  // PREVENTIVE, CORRECTIVE, etc.
    private String partsReplaced;
    private String workPerformed;
    private Integer hoursSpent;
    private String nextMaintenanceNotes;
}
