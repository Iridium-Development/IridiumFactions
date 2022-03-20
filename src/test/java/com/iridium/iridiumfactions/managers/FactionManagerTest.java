package com.iridium.iridiumfactions.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FactionManagerTest {

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
    public void getFactionViaId() {
        Faction faction1 = new FactionBuilder(1).build();
        Faction faction2 = new FactionBuilder(2).build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(1), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(2), faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(-1).getFactionType(), FactionType.WILDERNESS);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(-2).getFactionType(), FactionType.WARZONE);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(-3).getFactionType(), FactionType.SAFEZONE);
    }

    @Test
    public void getFactionViaName() {
        Faction faction1 = new FactionBuilder("Faction 1").build();
        Faction faction2 = new FactionBuilder("Faction 2").build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 2").orElse(null), faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("FACTION 2").orElse(null), faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Wilderness").get().getFactionType(), FactionType.WILDERNESS);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Warzone").get().getFactionType(), FactionType.WARZONE);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Safezone").get().getFactionType(), FactionType.SAFEZONE);
    }

    @Test
    public void getFactionViaLocation() {
        PlayerMock player = new UserBuilder(serverMock).build();

        Faction faction1 = new FactionBuilder().build();
        Faction faction2 = new FactionBuilder().build();

        Location claimLocation1 = new Location(player.getWorld(), 0, 0, 0);
        Location claimLocation2 = new Location(player.getWorld(), 100, 0, 100);

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction1, claimLocation1.getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction2, claimLocation2.getChunk()));

        assertEquals(faction1, IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(claimLocation1));
        assertEquals(faction2, IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(claimLocation2));
    }

    @Test
    public void createFaction() {
        PlayerMock player = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

        Faction faction = IridiumFactions.getInstance().getFactionManager().createFaction(player, "Faction").join();

        assertEquals(user.getFaction(), faction);
        assertEquals(user.getFactionRank(), FactionRank.OWNER);
    }

    @Test
    public void claimFactionLandNoPermissions() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WILDERNESS);
    }

    @Test
    public void claimFactionLandNotEnoughPower() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setPower(-10);
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WILDERNESS);
    }

    @Test
    public void claimFactionLandAlreadyClaimed() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().withMembers(1, 10, serverMock).build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setPower(10);
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(otherFaction, chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", otherFaction.getName())
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk), otherFaction);
    }

    @Test
    public void claimFactionLandAlreadyClaimedMinusPower() {
        Faction faction = new FactionBuilder().build();
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setPower(10);
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(otherFaction, chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
                .replace("%x%", String.valueOf(0))
                .replace("%z%", String.valueOf(0))
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk), faction);
    }

    @Test
    public void claimFactionLandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setPower(10);
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
                .replace("%x%", String.valueOf(0))
                .replace("%z%", String.valueOf(0))
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk), faction);
    }

    @Test
    public void overrideClaimToWilderness() {
        PlayerMock playerMock = new UserBuilder(serverMock).setBypassing().build();
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new FactionBuilder().build(), chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(new Faction(FactionType.WILDERNESS), playerMock.getLocation().getChunk(), playerMock).join();
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player0 has claimed land at (0,0)."));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WILDERNESS);
    }

    @Test
    public void overrideClaimToWarzone() {
        PlayerMock playerMock = new UserBuilder(serverMock).setBypassing().build();
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new FactionBuilder().build(), chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(new Faction(FactionType.WARZONE), playerMock.getLocation().getChunk(), playerMock).join();
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player0 has claimed land at (0,0)."));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WARZONE);
    }

    @Test
    public void overrideClaimToSafezone() {
        PlayerMock playerMock = new UserBuilder(serverMock).setBypassing().build();
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new FactionBuilder().build(), chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(new Faction(FactionType.SAFEZONE), playerMock.getLocation().getChunk(), playerMock).join();
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player0 has claimed land at (0,0)."));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.SAFEZONE);
    }

    @Test
    public void overrideClaimToOtherFaction() {
        Faction otherFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).setBypassing().build();
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new FactionBuilder().build(), chunk));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(otherFaction, playerMock.getLocation().getChunk(), playerMock).join();
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player0 has claimed land at (0,0)."));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk), otherFaction);
    }

    @Test
    public void claimFactionLandRadiusSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).withPower(10).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 2, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 9);
    }
    @Test
    public void claimFactionLandRadiusNoPermissions() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withPower(10).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 2, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 0);

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @Test
    public void claimFactionLandRadiusNotEnoughPower() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 2, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 0);

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @Test
    public void unClaimFactionLandSuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        Chunk chunk = playerMock.getLocation().getChunk();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, chunk));

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", faction.getName())
                .replace("%x%", String.valueOf(0))
                .replace("%z%", String.valueOf(0))
        ));
        assertEquals(FactionType.WILDERNESS, IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType());
    }

    @Test
    public void unClaimFactionLandNotClaimed() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionLandNotClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", faction.getName())
        ));
        assertEquals(FactionType.WILDERNESS, IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType());
    }

    @Test
    public void unClaimFactionLandNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        Chunk chunk = playerMock.getLocation().getChunk();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, chunk));

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        assertEquals(faction, IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk));
    }

    @Test
    public void unClaimAllFactionLandSuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 1));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 1));

        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 4);

        IridiumFactions.getInstance().getFactionManager().unClaimAllFactionLand(faction, playerMock).join();

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedAllLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", faction.getName())
        ));
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 0);
    }

    @Test
    public void unClaimAllFactionLandNoPermissions() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 1));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 1));

        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 4);

        IridiumFactions.getInstance().getFactionManager().unClaimAllFactionLand(faction, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 4);
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @Test
    public void deleteFaction() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 0));
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);

        IridiumFactions.getInstance().getFactionManager().deleteFaction(faction, user).join();

        assertEquals(user.getFactionID(), 0);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries().size(), 0);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 0);

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDisbanded
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
        ));
    }

    @Test
    public void setFactionPermission() {
        Faction faction = new FactionBuilder().build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, "permissionkey", true);

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, new Permission(null, 1, FactionRank.MODERATOR), "permissionkey"));
    }

    @Test
    public void getFactionRelationshipWilderness() {
        Faction faction = new FactionBuilder().build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(faction, new Faction(FactionType.WILDERNESS)), RelationshipType.WILDERNESS);
    }

    @Test
    public void getFactionRelationshipSafezone() {
        Faction faction = new FactionBuilder().build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(faction, new Faction(FactionType.SAFEZONE)), RelationshipType.SAFEZONE);
    }

    @Test
    public void getFactionRelationshipWarzone() {
        Faction faction = new FactionBuilder().build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(faction, new Faction(FactionType.WARZONE)), RelationshipType.WARZONE);
    }

    @Test
    public void getFactionRelationshipOwn() {
        Faction faction = new FactionBuilder().build();

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(faction, faction), RelationshipType.OWN);
    }

    @Test
    public void getFactionRelationshipTruce() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.TRUCE);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.TRUCE);
    }

    @Test
    public void getFactionRelationshipAlly() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ALLY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.ALLY);
    }

    @Test
    public void getFactionRelationshipEnemy() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ENEMY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.ENEMY);
    }

    @Test
    public void getFactionRankInTruceFaction() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).withFactionRank(FactionRank.OWNER).build()
        );

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.TRUCE);
    }

    @Test
    public void getFactionRankInEnemyFaction() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).withFactionRank(FactionRank.OWNER).build()
        );
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ENEMY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.ENEMY);
    }

    @Test
    public void getFactionRankInAllyFaction() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).withFactionRank(FactionRank.OWNER).build()
        );
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ALLY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.ALLY);
    }

    @Test
    public void getFactionRankInOwnFaction() {
        Faction faction = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build()
        );

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, faction), FactionRank.OWNER);
    }

    @Test
    public void getFactionRelationshipRequest() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());

        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY, user));

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionB, factionA, RelationshipType.ALLY).isPresent());
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ENEMY).isPresent());
    }

    @Test
    public void sendFactionRelationshipRequestAllyRequestAccepted() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionB).build()
        );
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY, otherUser));

        assertEquals(FactionRelationShipRequestResponse.REQUEST_ACCEPTED, IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, factionB, RelationshipType.ALLY));
        assertEquals(RelationshipType.ALLY, IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, factionB));
    }

    @Test
    public void sendFactionRelationshipRequestAllyRequestAlreadySent() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY, user));

        assertEquals(FactionRelationShipRequestResponse.ALREADY_SENT_REQUEST, IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, factionB, RelationshipType.ALLY));
    }

    @Test
    public void sendFactionRelationshipRequestAllyRequestSent() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );
        assertEquals(FactionRelationShipRequestResponse.REQUEST_SENT, IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, factionB, RelationshipType.ALLY));
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());

    }

    @Test
    public void sendFactionRelationshipRequestAlreadyEnemy() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ENEMY);

        assertEquals(FactionRelationShipRequestResponse.SAME_RELATIONSHIP, IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, factionB, RelationshipType.ENEMY));
        assertEquals(RelationshipType.ENEMY, IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, factionB));

    }

    @Test
    public void sendFactionRelationshipRequestSetEnemy() {
        Faction factionA = new FactionBuilder().build();
        Faction factionB = new FactionBuilder().build();

        User user = IridiumFactions.getInstance().getUserManager().getUser(
                new UserBuilder(serverMock).withFaction(factionA).build()
        );
        assertEquals(FactionRelationShipRequestResponse.SET, IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, factionB, RelationshipType.ENEMY));
        assertEquals(RelationshipType.ENEMY, IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, factionB));

    }

    @Test
    public void getFactionInvites() {
        User user = IridiumFactions.getInstance().getUserManager().getUser(new UserBuilder(serverMock).build());
        User inviter = IridiumFactions.getInstance().getUserManager().getUser(new UserBuilder(serverMock).build());
        Faction faction = new FactionBuilder().build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().addEntry(new FactionInvite(faction, user, inviter));

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionInvite(faction, user).isPresent());

    }

    @Test
    public void getFactionMembers() {
        Faction faction = new FactionBuilder().build();

        User user1 = IridiumFactions.getInstance().getUserManager().getUser(new UserBuilder(serverMock).withFaction(faction).build());
        User user2 = IridiumFactions.getInstance().getUserManager().getUser(new UserBuilder(serverMock).withFaction(faction).build());
        User user3 = IridiumFactions.getInstance().getUserManager().getUser(new UserBuilder(serverMock).withFaction(faction).build());

        List<User> users = IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction)
                .stream()
                .sorted(Comparator.comparing(User::getName))
                .toList();

        assertEquals(List.of(user1, user2, user3), users);
    }

    @Test
    public void getFactionWarps() {
        Faction faction1 = new FactionBuilder().build();
        Faction faction2 = new FactionBuilder().build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction1, new Location(null, 0, 0, 0), "Warp1"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction2, new Location(null, 0, 0, 0), "Warp2"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction2, new Location(null, 0, 0, 0), "Warp3"));

        assertEquals(Collections.singletonList("Warp1"), IridiumFactions.getInstance().getFactionManager().getFactionWarps(faction1).stream().map(FactionWarp::getName).collect(Collectors.toList()));
        assertEquals(Arrays.asList("Warp2", "Warp3"), IridiumFactions.getInstance().getFactionManager().getFactionWarps(faction2).stream().map(FactionWarp::getName).collect(Collectors.toList()));
    }

    @Test
    public void getFactionWarp() {
        Faction faction1 = new FactionBuilder().build();
        Faction faction2 = new FactionBuilder().build();

        FactionWarp factionWarp1 = new FactionWarp(faction1, new Location(null, 0, 0, 0), "Warp1");
        FactionWarp factionWarp2 = new FactionWarp(faction2, new Location(null, 0, 0, 0), "Warp2");

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp2);

        assertEquals(factionWarp1, IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction1, "Warp1").orElse(null));
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction1, "Warp2").isPresent());

        assertEquals(factionWarp2, IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction2, "Warp2").orElse(null));
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction2, "Warp1").isPresent());
    }

    @Test
    public void getFactionUpgradeDoesntExist() {
        Faction faction = new FactionBuilder().build();
        FactionUpgrade factionUpgrade = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, "upgrade");
        assertEquals(1, factionUpgrade.getLevel());
        assertEquals(Collections.singletonList(factionUpgrade), IridiumFactions.getInstance().getDatabaseManager().getFactionUpgradeTableManager().getEntries());
    }

    @Test
    public void getFactionUpgradeAlreadyExists() {
        Faction faction = new FactionBuilder().build();
        FactionUpgrade factionUpgrade = new FactionUpgrade(faction, "upgrade", 10);
        IridiumFactions.getInstance().getDatabaseManager().getFactionUpgradeTableManager().addEntry(factionUpgrade);
        assertEquals(10, IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, "upgrade").getLevel());
        assertEquals(Collections.singletonList(factionUpgrade), IridiumFactions.getInstance().getDatabaseManager().getFactionUpgradeTableManager().getEntries());
    }

    @Test
    public void getFactionChestInventoryItemsPersist() {
        Faction faction = new FactionBuilder().build();
        Inventory inventory = IridiumFactions.getInstance().getFactionManager().getFactionChestInventory(faction, 1);
        inventory.addItem(new ItemStack(Material.STONE));
        assertEquals(9, inventory.getSize());
        assertEquals(new ItemStack(Material.STONE), inventory.getItem(0));
    }

    @Test
    public void getFactionBankDoesntExist() {
        Faction faction = new FactionBuilder().build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().experienceBankItem);
        assertEquals(0, factionBank.getNumber());
        assertEquals(Collections.singletonList(factionBank), IridiumFactions.getInstance().getDatabaseManager().getFactionBankTableManager().getEntries());
    }

    @Test
    public void getFactionBankAlreadyExists() {
        Faction faction = new FactionBuilder().build();
        FactionBank factionBank = new FactionBank(faction, IridiumFactions.getInstance().getBankItems().experienceBankItem.getName(), 1000);
        IridiumFactions.getInstance().getDatabaseManager().getFactionBankTableManager().addEntry(factionBank);
        assertEquals(factionBank, IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().experienceBankItem));
        assertEquals(Collections.singletonList(factionBank), IridiumFactions.getInstance().getDatabaseManager().getFactionBankTableManager().getEntries());
    }

}