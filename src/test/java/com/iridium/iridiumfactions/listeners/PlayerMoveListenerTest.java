package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlayerMoveListenerTest {

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

    @Test
    public void moveToWildernessFromClaim() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        playerMock.simulatePlayerMove(new Location(playerMock.getWorld(), 100, 0, 100));

        assertEquals(StringUtils.color(RelationshipType.WILDERNESS.getColor()) + IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultName, playerMock.nextTitle());
        assertEquals(StringUtils.color(ChatColor.GRAY + IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultDescription), playerMock.nextSubTitle());
    }

    @Test
    public void moveToWildernessFromWilderness() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        playerMock.simulatePlayerMove(new Location(playerMock.getWorld(), 100, 0, 100));

        assertNull(playerMock.nextTitle());
        assertNull(playerMock.nextSubTitle());
    }

}