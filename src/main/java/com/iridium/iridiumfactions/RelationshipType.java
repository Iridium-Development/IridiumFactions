package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RelationshipType {
    OWN("&a", 4),
    ALLY("&d", 3),
    TRUCE("&7", 2),
    ENEMY("&c", 1),
    WILDERNESS("&2", -1),
    WARZONE("&c", -2),
    SAFEZONE("&e", -3);

    private String color;
    private int rank;

    public String getColor() {
        return IridiumFactions.getInstance().getConfiguration().factionRelationshipColors.getOrDefault(this, getDefaultColor());
    }

    public String getDefaultColor() {
        return color;
    }

    public int getRank() {
        return rank;
    }
}
