package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RelationshipType {
    OWN("&a", 2),
    ALLY("&d", 1),
    TRUCE("&7", 0),
    ENEMY("&c", -1);

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
