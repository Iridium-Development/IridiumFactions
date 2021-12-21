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

class RenameCommandTest {

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
    public void executeRenameCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f rename TheBestFaction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRenameCommandNoPermissions() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f rename TheBestFaction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangeName
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRenameCommandNameAlreadyExists() {
        new FactionBuilder("TheBestFaction").build();
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.RENAME.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f rename TheBestFaction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRenameCommandSuccessful() {
        Faction faction = new FactionBuilder("THEBESTFACTION").build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.RENAME.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f rename TheBestFaction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameChanged
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%name%", "TheBestFaction")));
        playerMock.assertNoMoreSaid();
    }

}