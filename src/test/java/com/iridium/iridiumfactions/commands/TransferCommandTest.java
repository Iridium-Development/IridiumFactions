package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.gui.ConfirmationGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferCommandTest {

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
    public void executeSetHomeCommandBadSyntax() {
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(new FactionBuilder().build()).build();

        serverMock.dispatchCommand(playerMock, "f transfer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().transferCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f transfer " + otherPlayer.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandNotOwner() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f transfer " + otherPlayer.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().mustBeOwnerToTransfer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandNotValidPlayer() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();

        serverMock.dispatchCommand(playerMock, "f transfer FakePlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandPlayerNotInFaction() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f transfer " + otherPlayer.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandCannotTransferYourself() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();

        serverMock.dispatchCommand(playerMock, "f transfer " + playerMock.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotTransferToYourself.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetHomeCommandSuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f transfer " + otherPlayer.getName());
        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof ConfirmationGUI);
        ConfirmationGUI confirmationGUI = (ConfirmationGUI) playerMock.getOpenInventory().getTopInventory().getHolder();
        confirmationGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().confirmationGUI.yes.slot, ClickType.LEFT, InventoryAction.UNKNOWN));

        assertEquals(FactionRank.CO_OWNER, IridiumFactions.getInstance().getUserManager().getUser(playerMock).getFactionRank());
        assertEquals(FactionRank.OWNER, IridiumFactions.getInstance().getUserManager().getUser(otherPlayer).getFactionRank());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionOwnershipTransferred
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%old_owner%", playerMock.getName())
                .replace("%new_owner%", otherPlayer.getName())
        ));
        playerMock.assertNoMoreSaid();
        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionOwnershipTransferred
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%old_owner%", playerMock.getName())
                .replace("%new_owner%", otherPlayer.getName())
        ));
        otherPlayer.assertNoMoreSaid();
    }


}