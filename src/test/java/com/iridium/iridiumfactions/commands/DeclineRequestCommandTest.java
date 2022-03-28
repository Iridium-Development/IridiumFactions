package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionRelationshipRequest;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeclineRequestCommandTest {

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
    public void executeDeclineCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f decline test");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeclineCommandUnknownFaction() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f decline test");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeclineCommandNoRequests() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f decline " + faction.getName());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noRequestsPresent
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", faction.getName())
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeclineCommandAllianceSuccessfullyDeclined() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayerMock = new UserBuilder(serverMock).withFaction(otherFaction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(otherFaction, faction, RelationshipType.ALLY, user));

        serverMock.dispatchCommand(playerMock, "f decline " + otherFaction.getName());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().requestDeclined
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "alliance")
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().yourRequestDeclined
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "alliance")
        ));
        otherPlayerMock.assertNoMoreSaid();

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction, otherFaction, RelationshipType.ALLY).isPresent());
    }

    @Test
    public void executeDeclineCommandAllianceSuccessfullyCanceled() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayerMock = new UserBuilder(serverMock).withFaction(otherFaction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(faction, otherFaction, RelationshipType.ALLY, user));

        serverMock.dispatchCommand(playerMock, "f decline " + otherFaction.getName());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().requestCanceled
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "alliance")
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().yourRequestsCanceled
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "alliance")
        ));
        otherPlayerMock.assertNoMoreSaid();

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction, otherFaction, RelationshipType.ALLY).isPresent());
    }

    @Test
    public void executeDeclineCommandTruceSuccessfullyDeclined() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayerMock = new UserBuilder(serverMock).withFaction(otherFaction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(otherFaction, faction, RelationshipType.TRUCE, user));

        serverMock.dispatchCommand(playerMock, "f decline " + otherFaction.getName());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().requestDeclined
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "truce")
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().yourRequestDeclined
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "truce")
        ));
        otherPlayerMock.assertNoMoreSaid();

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction, otherFaction, RelationshipType.TRUCE).isPresent());
    }

    @Test
    public void executeDeclineCommandTruceSuccessfullyCanceled() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayerMock = new UserBuilder(serverMock).withFaction(otherFaction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(faction, otherFaction, RelationshipType.TRUCE, user));

        serverMock.dispatchCommand(playerMock, "f decline " + otherFaction.getName());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().requestCanceled
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "truce")
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().yourRequestsCanceled
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
                .replace("%player%", playerMock.getName())
                .replace("%relationship%", "truce")
        ));
        otherPlayerMock.assertNoMoreSaid();

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction, otherFaction, RelationshipType.TRUCE).isPresent());
    }

}