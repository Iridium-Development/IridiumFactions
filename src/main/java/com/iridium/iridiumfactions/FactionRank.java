package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public String getDisplayName() {
        return IridiumFactions.getInstance().getConfiguration().factionRankNames.getOrDefault(this, this.name());
    }

    public static FactionRank getByLevel(int level) {
        for (FactionRank factionRank : values()) {
            if (factionRank.getLevel() == level) {
                return factionRank;
            }
        }
        return null;
    }
}
