package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
    public void getHeader() {
        PlayerMock player = serverMock.addPlayer("Player");
        assertEquals(new FactionsMap(player).getHeader(), "§8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8[ §c(0, 0) §7§2Wilderness §8]§8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m ");

        Faction faction = new Faction("Faction Name", 1);
        FactionClaim factionClaim = new FactionClaim(faction, "world", player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim);

        assertEquals(new FactionsMap(player).getHeader(), "§8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8[ §c(0, 0) §7Faction Name §8]§8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m §8§m ");
    }
}