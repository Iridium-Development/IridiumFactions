package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.gui.FactionUpgradeGUI;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeCommandTest {

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
    public void executeUpgradesCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f upgrades");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeUpgradesCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f upgrades");
        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof FactionUpgradeGUI);
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeUpgradesCommandUnknownUpgrade() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f upgrades unknownUpgrade");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownUpgrade.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeUpgradesCommandMaxLevel() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, UpgradeType.SPAWNER_UPGRADE).setLevel(1000);
        serverMock.dispatchCommand(playerMock, "f upgrades " + UpgradeType.SPAWNER_UPGRADE.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().maxLevelReached.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeUpgradesCommandCannotAfford() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().setEconomy(new TestEconomyProvider());

        serverMock.dispatchCommand(playerMock, "f upgrades " + UpgradeType.SPAWNER_UPGRADE.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotAfford.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeUpgradesCommandUpgradeSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        TestEconomyProvider testEconomyProvider = new TestEconomyProvider();
        IridiumFactions.getInstance().setEconomy(testEconomyProvider);
        testEconomyProvider.depositPlayer(playerMock, IridiumFactions.getInstance().getUpgrades().spawnerUpgrade.upgrades.get(2).money);

        serverMock.dispatchCommand(playerMock, "f upgrades " + UpgradeType.SPAWNER_UPGRADE.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().successfullyBoughtUpgrade
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%upgrade%", IridiumFactions.getInstance().getUpgrades().spawnerUpgrade.name)
                .replace("%cost%", IridiumFactions.getInstance().getNumberFormatter().format(IridiumFactions.getInstance().getUpgrades().spawnerUpgrade.upgrades.get(2).money))
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(0, testEconomyProvider.getBalance(playerMock));
        assertEquals(2, IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, UpgradeType.SPAWNER_UPGRADE).getLevel());
    }

    @Test
    public void upgradesCommandTabComplete(){
        assertEquals(Arrays.asList("warps", "chest", "spawner", "power", "experience"), IridiumFactions.getInstance().getCommands().upgradeCommand.onTabComplete(null, null, null, null));
    }
}