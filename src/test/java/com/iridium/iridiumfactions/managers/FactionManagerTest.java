package com.iridium.iridiumfactions.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FactionManagerTest {

    private ServerMock serverMock;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void getFactionViaId() {
        Faction faction1 = new Faction("Faction 1", 1);
        Faction faction2 = new Faction("Faction 2", 2);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(1).orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(2).orElse(null), faction2);
    }

    @Test
    public void getFactionViaName() {
        Faction faction1 = new Faction("Faction 1", 1);
        Faction faction2 = new Faction("Faction 2", 2);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 2").orElse(null), faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("FACTION 2").orElse(null), faction2);
    }

    @Test
    public void getFactionViaLocation() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Location location = mock(Location.class);
        Chunk chunk = mock(Chunk.class);
        when(chunk.getWorld()).thenReturn(world);
        when(chunk.getX()).thenReturn(0);
        when(chunk.getZ()).thenReturn(0).thenReturn(1);
        when(location.getChunk()).thenReturn(chunk);

        Faction faction1 = new Faction("Faction 1", 1);
        Faction faction2 = new Faction("Faction 2", 2);

        FactionClaim factionClaim1 = new FactionClaim(faction1, "world", 0, 0);
        FactionClaim factionClaim2 = new FactionClaim(faction2, "world", 0, 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction2);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(factionClaim2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(location).orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(location).orElse(null), faction2);
    }

    @Test
    public void createFaction() {
        Player player = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = IridiumFactions.getInstance().getFactionManager().createFaction(player, "Faction").join();
        assertEquals(user.getFaction().orElse(null), faction);
        assertEquals(user.getFactionRank(), FactionRank.OWNER);
    }

    @Test
    public void claimFactionLand() {
        Faction faction = mock(Faction.class);
        when(faction.getId()).thenReturn(1);
        when(faction.getName()).thenReturn("Faction");
        when(faction.getRemainingPower()).thenReturn(9999.00).thenReturn(0.00).thenReturn(99999.00);
        Player player = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Chunk chunk = mock(Chunk.class);
        when(chunk.getWorld()).thenReturn(world);
        when(chunk.getX()).thenReturn(0);
        when(chunk.getZ()).thenReturn(0);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, player).join();
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).isPresent());

        user.setFactionRank(FactionRank.OWNER);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, player).join();
        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).isPresent());

        user.setBypassing(true);

        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, player).join();
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(chunk).isPresent());
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);

        // Should still have size 1 since we cant claim land twice
        IridiumFactions.getInstance().getFactionManager().claimFactionLand(faction, chunk, player).join();
        assertEquals(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries().size(), 1);
    }

    @Test
    public void claimFactionLandRadius() {
        Faction faction = new Faction("Faction", 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        Player player = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

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

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(factionA, null), RelationshipType.TRUCE);
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
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(factionA, factionB, RelationshipType.ALLY).isPresent());
    }

}