package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactionRankTest {

    private ServerMock serverMock;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void getDisplayName() {
        Map<FactionRank, String> factionRankNames = IridiumFactions.getInstance().getConfiguration().factionRankNames;
        assertEquals(FactionRank.OWNER.getDisplayName(), factionRankNames.get(FactionRank.OWNER));
        assertEquals(FactionRank.CO_OWNER.getDisplayName(), factionRankNames.get(FactionRank.CO_OWNER));
        assertEquals(FactionRank.MODERATOR.getDisplayName(), factionRankNames.get(FactionRank.MODERATOR));
        assertEquals(FactionRank.MEMBER.getDisplayName(), factionRankNames.get(FactionRank.MEMBER));
        assertEquals(FactionRank.TRUCE.getDisplayName(), factionRankNames.get(FactionRank.TRUCE));
        assertEquals(FactionRank.ALLY.getDisplayName(), factionRankNames.get(FactionRank.ALLY));
        assertEquals(FactionRank.ENEMY.getDisplayName(), factionRankNames.get(FactionRank.ENEMY));
    }

    @Test
    public void getByLevel() {
        assertEquals(FactionRank.getByLevel(4), FactionRank.OWNER);
        assertEquals(FactionRank.getByLevel(3), FactionRank.CO_OWNER);
        assertEquals(FactionRank.getByLevel(2), FactionRank.MODERATOR);
        assertEquals(FactionRank.getByLevel(1), FactionRank.MEMBER);
        assertEquals(FactionRank.getByLevel(0), FactionRank.TRUCE);
        assertEquals(FactionRank.getByLevel(-1), FactionRank.ALLY);
        assertEquals(FactionRank.getByLevel(-2), FactionRank.ENEMY);
    }
}