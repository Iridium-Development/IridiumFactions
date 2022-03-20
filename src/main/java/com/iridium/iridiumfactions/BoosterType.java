package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoosterType {
    FLIGHT_BOOSTER("flight"),
    SPAWNER_BOOSTER("spawner");

    private final String name;
}
