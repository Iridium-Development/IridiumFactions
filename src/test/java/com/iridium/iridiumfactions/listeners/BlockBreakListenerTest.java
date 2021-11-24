package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockBreakListenerTest {

    private ServerMock serverMock;

    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(IridiumFactions.getInstance());
        MockBukkit.unmock();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void onBlockBreak() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Location location = playerMock.getLocation();
        Faction faction = new Faction("Faction", 1);
        FactionClaim factionClaim = new FactionClaim(faction, location.getChunk());

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim);

        assertTrue(playerMock.simulateBlockBreak(location.getBlock()).isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotBreakBlocks
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));

        user.setBypassing(true);

        assertFalse(playerMock.simulateBlockBreak(location.getBlock()).isCancelled());

    }
}