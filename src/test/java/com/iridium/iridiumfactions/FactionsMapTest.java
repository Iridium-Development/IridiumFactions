package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactionsMapTest {

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
    public void getFactionCharacter() {
        Player player = serverMock.addPlayer("Player");
        FactionsMap factionsMap = new FactionsMap(player);
        int factionID = 1;
        for (char character : IridiumFactions.getInstance().getConfiguration().mapChars) {
            assertEquals(factionsMap.getFactionCharacter(new Faction("faction", factionID)), character);
            factionID++;
        }
        assertEquals(factionsMap.getFactionCharacter(new Faction("faction", factionID)), '\\');
    }

    @Test
    public void getHeaderWilderness() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        assertEquals(StringUtils.color("&8&m ".repeat(26) + "&8[ &c(0, 0) &2Wilderness &8]" + "&8&m ".repeat(26)), new FactionsMap(playerMock).getHeader());
    }

    @Test
    public void getHeaderFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        Faction faction = new FactionBuilder("Faction").build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", playerMock.getLocation().getChunk().getX(), playerMock.getLocation().getChunk().getZ()));

        assertEquals(StringUtils.color("&8&m ".repeat(28) + "&8[ &c(0, 0) &7" + faction.getName() + " &8]" + "&8&m ".repeat(28)), new FactionsMap(playerMock).getHeader());
    }
}