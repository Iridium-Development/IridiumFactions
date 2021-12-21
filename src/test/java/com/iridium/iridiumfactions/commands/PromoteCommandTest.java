package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PromoteCommandTest {

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
    public void executePromoteCommandBadSyntax() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f promote");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().promoteCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandUserNotInFaction() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandCannotPromote_HighestRank() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.OWNER, PermissionType.PROMOTE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPromoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandCannotPromote_NoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.OWNER, PermissionType.PROMOTE.getPermissionKey(), false);

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPromoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandCannotPromote_HigherRank() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.PROMOTE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPromoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.CO_OWNER).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.PROMOTE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f promote " + otherPlayer.getDisplayName());

        assertEquals(FactionRank.MODERATOR, IridiumFactions.getInstance().getUserManager().getUser(otherPlayer).getFactionRank());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().promotedPlayer
                .replace("%player%", otherPlayer.getName())
                .replace("%rank%", FactionRank.MODERATOR.getDisplayName())
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userPromotedPlayer
                .replace("%promoter%", playerMock.getName())
                .replace("%player%", otherPlayer.getName())
                .replace("%rank%", FactionRank.MODERATOR.getDisplayName())
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        otherPlayer.assertNoMoreSaid();
    }

}