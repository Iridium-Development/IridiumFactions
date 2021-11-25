package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinCommandTest {

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
    public void executeJoinCommandBadSyntax() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        serverMock.dispatchCommand(playerMock, "f join");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().joinCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeJoinCommandInFaction() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f join Faction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeJoinCommandFactionDoesntExist() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        serverMock.dispatchCommand(playerMock, "f join Faction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeJoinCommandNoInvite() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(new Faction("Faction", 1));

        serverMock.dispatchCommand(playerMock, "f join Faction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeJoinCommandBypassing() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        PlayerMock factionMember = serverMock.addPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setBypassing(true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getUserManager().getUser(factionMember).setFaction(faction);

        serverMock.dispatchCommand(playerMock, "f join Faction");
        assertEquals(user.getFactionID(), 1);
        assertEquals(user.getFactionRank(), FactionRank.MEMBER);

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().joinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
        ));
        playerMock.assertNoMoreSaid();

        factionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userJoinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", user.getFaction().getName())
                .replace("%player%", playerMock.getName())
        ));
        factionMember.assertNoMoreSaid();
    }

    @Test
    public void executeJoinCommandInvited() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        PlayerMock factionMember = serverMock.addPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().addEntry(new FactionInvite(faction, user, user));
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getUserManager().getUser(factionMember).setFaction(faction);

        serverMock.dispatchCommand(playerMock, "f join Faction");
        assertEquals(user.getFactionID(), 1);
        assertEquals(user.getFactionRank(), FactionRank.MEMBER);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().getEntries().size(), 0);

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().joinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
        ));
        playerMock.assertNoMoreSaid();

        factionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userJoinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", user.getFaction().getName())
                .replace("%player%", playerMock.getName())
        ));
        factionMember.assertNoMoreSaid();
    }

}