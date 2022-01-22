package com.iridium.iridiumfactions.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.managers.tablemanagers.ForeignFactionTableManager;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void claimFactionLand() {
        Faction faction = new Faction("Faction", 1);
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Chunk chunk = mock(Chunk.class);
        when(chunk.getWorld()).thenReturn(world);
        when(chunk.getX()).thenReturn(0);
        when(chunk.getZ()).thenReturn(0);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        // Cannot Claim land Due to permissions
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WILDERNESS);

        user.setFactionRank(FactionRank.OWNER);
        user.setFaction(faction);

        for (int i = 0; i < 100; i++) {
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "", 0, 0));
        }

        // Cannot Claim land due to lack of power
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.WILDERNESS);

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().clear();

        // Can successfully claim land
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
                .replace("%x%", String.valueOf(0))
                .replace("%z%", String.valueOf(0))
        ));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).getFactionType(), FactionType.PLAYER_FACTION);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);

        // Cant claim the same land twice
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, playerMock).join();
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", faction.getName())
        ));
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);
    }

    @Test
    public void claimWarzoneThenWilderness() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        user.setBypassing(true);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(new Faction(FactionType.WARZONE), playerMock.getLocation().getChunk(), playerMock).join();
        assertEquals(1, IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size());
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player has claimed land at (0,0)."));

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(new Faction(FactionType.WILDERNESS), playerMock.getLocation().getChunk(), playerMock).join();
        assertEquals(0, IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size());
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7Player has claimed land at (0,0)."));
    }

    @Test
    public void claimFactionLandRadius() {
        Faction faction = new Faction("Faction", 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        PlayerMock player = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

        user.setFaction(faction);
        user.setBypassing(true);

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Chunk chunk = mock(Chunk.class);
        when(chunk.getWorld()).thenReturn(world);
        when(chunk.getX()).thenReturn(0);
        when(chunk.getZ()).thenReturn(0);

        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 0);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 1, player).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 2, player).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 9);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 3, player).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 25);

        while (true) {
            if (player.nextMessage() == null) break;
        }

        user.setBypassing(false);
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 1, player).join();
        assertEquals(player.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));

        user.setFactionRank(FactionRank.OWNER);
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, 1, player).join();
        assertEquals(player.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @Test
    public void unClaimFactionLand() {
        Faction faction = new Faction("Faction", 1);
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Chunk chunk = mock(Chunk.class);
        when(chunk.getWorld()).thenReturn(world);
        when(chunk.getX()).thenReturn(0);
        when(chunk.getZ()).thenReturn(0);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, chunk));

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 1);
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));

        user.setFactionRank(FactionRank.OWNER);

        when(chunk.getZ()).thenReturn(1);

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 1);
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().factionLandNotClaim
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", faction.getName())
        ));

        when(chunk.getZ()).thenReturn(0);

        IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, chunk, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 0);
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
                .replace("%x%", String.valueOf(0))
                .replace("%z%", String.valueOf(0))
        ));
    }

    @Test
    public void unClaimAllFactionLand() {
        Faction faction = new Faction("Faction", 1);
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 1));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 0));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 1, 1));

        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 4);

        IridiumFactions.getInstance().getFactionManager().unClaimAllFactionLand(faction, playerMock).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 4);
        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));

        user.setFactionRank(FactionRank.OWNER);

        IridiumFactions.getInstance().getFactionManager().unClaimAllFactionLand(faction, playerMock).join();

        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedAllLand
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", faction.getName())
        ));
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size(), 0);
    }

    @Test
    public void deleteFaction() {
        Faction faction = new Faction("Faction", 1);
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setFaction(faction);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, "world", 0, 0));
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);

        IridiumFactions.getInstance().getFactionManager().deleteFaction(faction, user).join();

        assertEquals(user.getFactionID(), 0);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries().size(), 0);
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 0);

        assertEquals(playerMock.nextMessage(), StringUtils.color(IridiumFactions.getInstance().getMessages().factionDisbanded
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", user.getName())
                .replace("%faction%", faction.getName())
        ));

        user.setBypassing(true);
    }

    @Test
    public void getFactionPermission() {
        String permissionkey = "permissionkey";
        Faction faction = new Faction("Faction", 1);
        User user = new User(UUID.randomUUID(), "");
        Permission permission = new Permission(null, 1, FactionRank.MODERATOR);

        user.setBypassing(false);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, permission, permissionkey));

        user.setBypassing(true);
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, permission, permissionkey));
        user.setBypassing(false);

        user.setFactionRank(FactionRank.MODERATOR);
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, permission, permissionkey));

        FactionPermission factionPermission = new FactionPermission(faction, permissionkey, user.getFactionRank(), false);
        IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().addEntry(factionPermission);
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, permission, permissionkey));

        factionPermission.setAllowed(true);
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, permission, permissionkey));
    }

    @Test
    public void setFactionPermission() {
        String permissionkey = "permissionkey";
        Faction faction = new Faction("Faction", 1);
        FactionRank factionRank = FactionRank.OWNER;

        assertFalse(IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, permissionkey, factionRank, true)).isPresent());

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, factionRank, permissionkey, true);
        assertTrue(IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, permissionkey, factionRank, true)).isPresent());
        assertTrue(IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, permissionkey, factionRank, true)).get().isAllowed());

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, factionRank, permissionkey, false);
        assertTrue(IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, permissionkey, factionRank, true)).isPresent());
        assertFalse(IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, permissionkey, factionRank, true)).get().isAllowed());
    }

    @Test
    public void getFactionRelationship() {
        Faction factionA = new Faction("FactionA", 1);
        Faction factionB = new Faction("FactionB", 2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(new Faction(FactionType.WILDERNESS), factionA), RelationshipType.TRUCE);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, new Faction(FactionType.WILDERNESS)), RelationshipType.WILDERNESS);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, new Faction(FactionType.WARZONE)), RelationshipType.WARZONE);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, new Faction(FactionType.SAFEZONE)), RelationshipType.SAFEZONE);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionA), RelationshipType.OWN);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.TRUCE);

        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().addEntry(new FactionRelationship(factionA, factionB, RelationshipType.ALLY));
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.ALLY);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionB, factionA), RelationshipType.ALLY);
    }

    @Test
    public void setFactionRelationship() {
        Faction factionA = new Faction("FactionA", 1);
        Faction factionB = new Faction("FactionB", 2);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.TRUCE);

        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ALLY);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.ALLY);

        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionB, factionA, RelationshipType.ENEMY);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, factionB), RelationshipType.ENEMY);
    }

    @Test
    public void getFactionRank() {
        Faction factionA = new Faction("FactionA", 1);
        Faction factionB = new Faction("FactionB", 2);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(factionA);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(factionB);
        User user = new User(UUID.randomUUID(), "");
        user.setFaction(factionA);
        user.setFactionRank(FactionRank.CO_OWNER);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.TRUCE);

        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ALLY);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.ALLY);

        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(factionA, factionB, RelationshipType.ENEMY);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionB), FactionRank.ENEMY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRank(user, factionA), user.getFactionRank());
    }

    @Test
    public void getFactionRelationshipRequest() {
        Faction factionA = new Faction("FactionA", 1);
        Faction factionB = new Faction("FactionB", 2);
        User user = new User(UUID.randomUUID(), "");

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());

        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY, user));
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionB, factionA, RelationshipType.ALLY).isPresent());
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ENEMY).isPresent());
    }

    @Test
    public void sendFactionRelationshipRequest() {
        Faction factionA = new Faction("FactionA", 1);
        Faction factionB = new Faction("FactionB", 2);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(factionA);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(factionB);
        User userA = new User(UUID.randomUUID(), "");
        userA.setFaction(factionA);
        User userB = new User(UUID.randomUUID(), "");
        userB.setFaction(factionB);
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(userA);
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(userB);

        assertEquals(IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(userA, factionB, RelationshipType.ENEMY), FactionRelationShipRequestResponse.SET);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(userA, factionB), RelationshipType.ENEMY);

        assertEquals(IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(userA, factionB, RelationshipType.ENEMY), FactionRelationShipRequestResponse.SAME_RELATIONSHIP);

        assertEquals(IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(userA, factionB, RelationshipType.ALLY), FactionRelationShipRequestResponse.REQUEST_SENT);
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());

        assertEquals(IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(userA, factionB, RelationshipType.ALLY), FactionRelationShipRequestResponse.ALREADY_SENT_REQUEST);
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());

        assertEquals(IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(userB, factionA, RelationshipType.ALLY), FactionRelationShipRequestResponse.REQUEST_ACCEPTED);
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(userA, factionB), RelationshipType.ALLY);
    }

    @Test
    public void getFactionInvites() {
        User user = new User(UUID.randomUUID(), "User");
        User inviter = new User(UUID.randomUUID(), "Inviter");
        Faction faction = new Faction("Faction", 1);
        ForeignFactionTableManager<FactionInvite, Integer> factionInviteTableManager = IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager();

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        FactionInvite factionInvite = new FactionInvite(faction, user, inviter);
        factionInviteTableManager.addEntry(factionInvite);
        factionInviteTableManager.addEntry(new FactionInvite(faction, new User(UUID.randomUUID(), ""), new User(UUID.randomUUID(), "")));
        factionInviteTableManager.addEntry(new FactionInvite(faction, new User(UUID.randomUUID(), ""), new User(UUID.randomUUID(), "")));
        factionInviteTableManager.addEntry(new FactionInvite(faction, new User(UUID.randomUUID(), ""), new User(UUID.randomUUID(), "")));

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionInvite(faction, user).orElse(null), factionInvite);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionInvites(faction).size(), 4);

    }

    @Test
    public void getFactionMembers() {
        Faction faction = new Faction("Faction", 1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        PlayerMock playerMock1 = serverMock.addPlayer("Player1");
        PlayerMock playerMock2 = serverMock.addPlayer("Player2");
        PlayerMock playerMock3 = serverMock.addPlayer("Player3");

        User user1 = IridiumFactions.getInstance().getUserManager().getUser(playerMock1);
        User user2 = IridiumFactions.getInstance().getUserManager().getUser(playerMock2);
        User user3 = IridiumFactions.getInstance().getUserManager().getUser(playerMock3);

        user1.setFaction(faction);
        user2.setFaction(faction);
        user3.setFaction(faction);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).size(), 3);

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).contains(user1));
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).contains(user2));
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).contains(user3));

        user3.setFaction(null);
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).contains(user3));
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