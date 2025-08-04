package com.ali.amara.equipment.entity;

public enum MaintenanceType {
    PREVENTIVE("Maintenance préventive"),
    CORRECTIVE("Maintenance corrective"),
    EMERGENCY("Réparation d'urgence"),
    INSPECTION("Inspection régulière"),
    UPGRADE("Mise à niveau"),
    CALIBRATION("Calibration"),
    OIL_CHANGE("Vidange"),
    PARTS_REPLACEMENT("Remplacement de pièces"),
    SOFTWARE_UPDATE("Mise à jour logicielle"),
    CLEANING("Nettoyage"),
    ROUTINE("Nettoyage");

    private final String description;

    MaintenanceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
