package com.ali.amara.reaction.enums;

import lombok.Getter;

@Getter
public enum ReactionType {
    FERTILIZE("Fertilize", "🌱", 1),
    IRRIGATE("Irrigate", "💧", 5),
    HARVEST("Harvest", "🏆", 3),
    TREAT("Treat", "🧪", -2),
    DROUGHT("Drought", "☀️", -3);

    private final String displayName;
    private final String emoji;
    private final int engagementScore;

    ReactionType(String displayName, String emoji, int engagementScore) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.engagementScore = engagementScore;
    }
}
