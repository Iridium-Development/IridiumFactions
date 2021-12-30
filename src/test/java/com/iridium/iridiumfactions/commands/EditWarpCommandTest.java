package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EditWarpCommandTest {

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
    public void executeEditWarpCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f editwarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f editwarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().editWarpCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f editwarp test icon BEDROCK");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotEditWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandWarpDoestExist() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f editwarp test icon BEDROCK");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandBadEditType() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test UnknownEditType");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().editWarpCommand.syntax
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandIconNoMaterial() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test icon");

        playerMock.assertSaid(StringUtils.color(StringUtils.color("%prefix% &7/f editwarp test icon <icon>")
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandIconError() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test icon BadMaterialName");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noSuchMaterial
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%material%", "BadMaterialName")
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandIconSuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test icon Bedrock");

        assertEquals(XMaterial.BEDROCK, factionWarp.getIcon());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().warpIconSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandDescriptionSuccessNoDescription() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");
        factionWarp.setDescription("Test Description");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test description");

        assertNull(factionWarp.getDescription());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().warpDescriptionRemoved
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEditWarpCommandDescriptionSuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();
        FactionWarp factionWarp = new FactionWarp(faction, playerMock.getLocation(), "test");

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);

        serverMock.dispatchCommand(playerMock, "f editwarp test description Cool New MultiWord Description");

        assertEquals("Cool New MultiWord Description", factionWarp.getDescription());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().warpDescriptionSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%description%", "Cool New MultiWord Description")
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void editWarpCommandTabComplete() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "home"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "farm"));
        assertEquals(Arrays.asList("farm", "home"), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", ""}));
        assertEquals(List.of("farm"), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", "f"}));
        assertEquals(Arrays.asList("icon", "description"), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", "farm", ""}));
        assertEquals(Collections.emptyList(), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", "farm", "description", ""}));
        assertEquals(Arrays.stream(XMaterial.values()).map(XMaterial::name).collect(Collectors.toList()), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", "farm", "icon", ""}));
        assertEquals(List.of("BEDROCK"), IridiumFactions.getInstance().getCommands().editWarpCommand.onTabComplete(playerMock, null, null, new String[]{"warp", "farm", "icon", "bedro"}));
    }
}