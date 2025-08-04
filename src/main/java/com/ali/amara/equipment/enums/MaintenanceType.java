package com.ali.amara.equipment.enums;

/**
 * Types de maintenance possibles pour les équipements
 */
public enum MaintenanceType {
    ROUTINE("Maintenance régulière"),         // Maintenance régulière programmée
    PREVENTIVE("Maintenance préventive"),     // Maintenance préventive
    INSPECTION("Inspection réglementaire"),   // Inspection réglementaire
    OIL_CHANGE("Vidange"),                   // Vidange
    REPAIRS("Réparations"),                  // Réparations
    CALIBRATION("Calibrage"),                // Calibrage
    PARTS_REPLACEMENT("Remplacement pièces"), // Remplacement de pièces
    SOFTWARE_UPDATE("Mise à jour logicielle"),// Mise à jour logicielle
    CLEANING("Nettoyage");                   // Nettoyage

    private final String description;

    MaintenanceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
