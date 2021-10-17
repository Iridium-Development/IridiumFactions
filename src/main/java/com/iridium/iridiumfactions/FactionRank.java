package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FactionRank {
    OWNER(4),
    CO_OWNER(3),
    MODERATOR(2),
    MEMBER(1),
    TRUCE(0),
    ALLY(-1),
    ENEMY(-2);
    private final int level;

}
