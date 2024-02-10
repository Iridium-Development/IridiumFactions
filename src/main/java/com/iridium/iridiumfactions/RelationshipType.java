package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RelationshipType {
    OWN("&a"),
    ALLY("&d"),
    TRUCE("&7"),
    ENEMY("&c"),
    WILDERNESS("&2"),
    WARZONE("&c"),
    SAFEZONE("&e");

    private final String color;

    public String getColor() {
        return IridiumFactions.getInstance().getConfiguration().factionRelationshipColors.getOrDefault(this, getDefaultColor());
    }

    public String getDefaultColor() {
        return color;
    }
}
