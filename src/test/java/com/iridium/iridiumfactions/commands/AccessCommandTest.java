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
import com.iridium.iridiumfactions.database.FactionAccess;
import com.iridium.iridiumfactions.database.FactionClaim;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AccessCommandTest {


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
    public void executeAccessCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        serverMock.dispatchCommand(playerMock, "f access");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandNotInClaim() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f access");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandInvalidSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionClaim factionClaim = new FactionClaim(faction, playerMock.getLocation().getChunk());
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim);

        serverMock.dispatchCommand(playerMock, "f access owner");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().accessCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandListAccessSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f access");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAccessListHeader.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        for (FactionRank factionRank : FactionRank.values()) {
            playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionRankAccess
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%rank%", factionRank.getDisplayName())
                    .replace("%access%", "ALLOWED")
            ));
        }
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandInvalidFactionRank() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f access invalid allow");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownFactionRank.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandInvalidAllowDeny() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f access owner invalid");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().accessCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAccessCommandSetAccessAllow() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionClaim factionClaim = new FactionClaim(faction, playerMock.getLocation().getChunk());
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim);
        IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().addEntry(new FactionAccess(faction, factionClaim, FactionRank.OWNER, false));

        serverMock.dispatchCommand(playerMock, "f access owner allow");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAccessSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%access%", "allowed")
                .replace("%rank%", FactionRank.OWNER.getDisplayName())
        ));
        playerMock.assertNoMoreSaid();
        assertTrue(IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().getEntry(new FactionAccess(faction, factionClaim, FactionRank.OWNER, false)).map(FactionAccess::isAllowed).orElse(false));
    }

    @Test
    public void executeAccessCommandSetAccessDenied() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionClaim factionClaim = new FactionClaim(faction, playerMock.getLocation().getChunk());
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim);

        serverMock.dispatchCommand(playerMock, "f access owner deny");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAccessSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%access%", "denied")
                .replace("%rank%", FactionRank.OWNER.getDisplayName())
        ));
        playerMock.assertNoMoreSaid();
        assertFalse(IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().getEntry(new FactionAccess(faction, factionClaim, FactionRank.OWNER, false)).map(FactionAccess::isAllowed).orElse(true));
    }

    @Test
    public void accessCommandTabComplete() {
        assertEquals(Arrays.asList("Owner", "CoOwner", "Moderator", "Member", "Truce", "Ally", "Enemy"), IridiumFactions.getInstance().getCommands().accessCommand.onTabComplete(null, null, null, new String[]{"access", ""}));
        assertEquals(Arrays.asList("allow", "deny"), IridiumFactions.getInstance().getCommands().accessCommand.onTabComplete(null, null, null, new String[]{"access", "", ""}));
    }
}