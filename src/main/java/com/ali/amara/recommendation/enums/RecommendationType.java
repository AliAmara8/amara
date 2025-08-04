package com.ali.amara.recommendation.enums;

public enum RecommendationType {
    FARM_TYPE("Basé sur le type d'exploitation"),
    LOCATION("Basé sur la localisation"),
    CROPS("Basé sur les cultures"),
    EQUIPMENT("Basé sur l'équipement"),
    MIXED("Combinaison de facteurs");

    private final String description;

    RecommendationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
