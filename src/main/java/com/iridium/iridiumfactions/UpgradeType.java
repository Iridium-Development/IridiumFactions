package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UpgradeType {
    CHEST_UPGRADE("chest"),
    EXPERIENCE_UPGRADE("experience"),
    POWER_UPGRADE("power"),
    SPAWNER_UPGRADE("spawner"),
    WARPS_UPGRADE("warps");

    private final String name;
}
