package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.configs.BlockValues;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.utils.LocationUtils;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FactionManager {

    public Optional<Faction> getFactionViaId(int id) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id);
    }

    public Optional<Faction> getFactionViaName(String name) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(name);
    }

    public Optional<Faction> getFactionViaLocation(Location location) {
        return getFactionViaChunk(location.getChunk());
    }

    public Optional<Faction> getFactionViaChunk(Chunk chunk) {
        return getFactionViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public Optional<Faction> getFactionViaChunk(World world, int x, int z) {
        int factionID = IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager()
                .getEntry(new FactionClaim(new Faction(""), world.getName(), x, z))
                .map(FactionData::getFactionID)
                .orElse(0);
        return getFactionViaId(factionID);
    }

    private Optional<FactionClaim> getFactionClaimViaChunk(Chunk chunk) {
        return getFactionClaimViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    private Optional<FactionClaim> getFactionClaimViaChunk(World world, int x, int z) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager()
                .getEntry(new FactionClaim(new Faction(""), world.getName(), x, z));
    }

    public CompletableFuture<Faction> createFaction(@NotNull Player owner, @NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(owner);
            Faction faction = new Faction(name);

            IridiumFactions.getInstance().getDatabaseManager().registerFaction(faction).join();

            user.setFaction(faction);
            user.setFactionRank(FactionRank.OWNER);

            return faction;
        });
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, Chunk chunk, Player player) {
        return claimFactionLand(faction, chunk.getWorld(), chunk.getX(), chunk.getZ(), player);
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, World world, int x, int z, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getFactionPermission(faction, user, PermissionType.CLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            if (faction.getRemainingPower() < 1 && !user.isBypassing()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            Optional<Faction> factionClaimedAtLand = getFactionViaChunk(world, x, z);
            if (factionClaimedAtLand.isPresent()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%faction%", factionClaimedAtLand.get().getName())
                ));
                return;
            }
            getFactionMembers(faction).forEach(user1 -> {
                Player p = user1.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", user.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%x%", String.valueOf(x))
                            .replace("%z%", String.valueOf(z))
                    ));
                }
            });
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, world.getName(), x, z));
        });
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getFactionPermission(faction, user, PermissionType.CLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            World world = centerChunk.getWorld();
            for (int x = centerChunk.getX() - (radius - 1); x <= centerChunk.getX() + (radius - 1); x++) {
                for (int z = centerChunk.getZ() - (radius - 1); z <= centerChunk.getZ() + (radius - 1); z++) {
                    if (faction.getRemainingPower() < 1 && !user.isBypassing()) {
                        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        ));
                        return;
                    }
                    claimFactionLand(faction, world, x, z, player).join();
                }
            }
        });
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, Chunk chunk, Player player) {
        return unClaimFactionLand(faction, chunk.getWorld(), chunk.getX(), chunk.getZ(), player);
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, World world, int x, int z, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getFactionPermission(faction, user, PermissionType.UNCLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            Optional<FactionClaim> factionClaim = getFactionClaimViaChunk(world, x, z);
            Optional<Faction> factionClaimedAtLand = getFactionViaId(factionClaim.map(FactionData::getFactionID).orElse(0));
            if (!factionClaim.isPresent() || !factionClaimedAtLand.isPresent() || factionClaimedAtLand.get().getId() != faction.getId()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionLandNotClaim
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%faction%", faction.getName())
                ));
                return;
            }
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(factionClaim.get());
            getFactionMembers(faction).forEach(user1 -> {
                Player p = user1.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", user.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%x%", String.valueOf(x))
                            .replace("%z%", String.valueOf(z))
                    ));
                }
            });
        });
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getFactionPermission(faction, user, PermissionType.UNCLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            for (FactionClaim factionClaim : IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction)) {
                if (factionClaim.getX() > centerChunk.getX() - radius && factionClaim.getX() < centerChunk.getX() + radius) {
                    if (factionClaim.getZ() > centerChunk.getZ() - radius && factionClaim.getZ() < centerChunk.getZ() + radius) {
                        if (factionClaim.getWorld().equals(centerChunk.getWorld().getName())) {
                            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(factionClaim).join();
                            getFactionMembers(faction).forEach(user1 -> {
                                Player p = user1.getPlayer();
                                if (p != null) {
                                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                            .replace("%player%", user.getName())
                                            .replace("%faction%", faction.getName())
                                            .replace("%x%", String.valueOf(factionClaim.getX()))
                                            .replace("%z%", String.valueOf(factionClaim.getZ()))
                                    ));
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public CompletableFuture<Void> unClaimAllFactionLand(Faction faction, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getFactionPermission(faction, user, PermissionType.UNCLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUnClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction));
            getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                    member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedAllLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", player.getName())
                            .replace("%faction%", faction.getName())
                    ))
            );
        });
    }

    public CompletableFuture<Void> deleteFaction(Faction faction, User user) {
        return CompletableFuture.runAsync(() -> {
            getFactionMembers(faction).forEach(user1 -> {
                Player player = user1.getPlayer();
                if (player != null) {
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDisbanded
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", user.getName())
                            .replace("%faction%", faction.getName())
                    ));
                }
                user1.setFaction(null);
            });
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction));
            IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().delete(faction);
        });
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull FactionRank factionRank, @NotNull Permission permission, @NotNull String key) {
        Optional<FactionPermission> factionPermission = IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, key, factionRank, true));
        return factionPermission.map(FactionPermission::isAllowed).orElseGet(() -> factionRank.getLevel() >= permission.getDefaultRank().getLevel());
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull Permission permission, @NotNull String key) {
        return user.isBypassing() || getFactionPermission(faction, getFactionRank(user, faction), permission, key);
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull PermissionType permissionType) {
        return getFactionPermission(faction, user, IridiumFactions.getInstance().getPermissionList().get(permissionType.getPermissionKey()), permissionType.getPermissionKey());
    }

    public synchronized void setFactionPermission(@NotNull Faction faction, @NotNull FactionRank factionRank, @NotNull String key, boolean allowed) {
        Optional<FactionPermission> factionPermission = IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, key, factionRank, allowed));
        if (factionPermission.isPresent()) {
            factionPermission.get().setAllowed(allowed);
        } else {
            IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().addEntry(new FactionPermission(faction, key, factionRank, allowed));
        }
    }

    public RelationshipType getFactionRelationship(Faction a, Faction b) {
        if (a == null || b == null) {
            return RelationshipType.TRUCE;
        }
        if (a == b) {
            return RelationshipType.OWN;
        }
        Optional<FactionRelationship> factionRelationshipA = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(a, b));
        if (factionRelationshipA.isPresent()) {
            return factionRelationshipA.get().getRelationshipType();
        }
        Optional<FactionRelationship> factionRelationshipB = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(b, a));
        if (factionRelationshipB.isPresent()) {
            return factionRelationshipB.get().getRelationshipType();
        }
        return RelationshipType.TRUCE;
    }

    public RelationshipType getFactionRelationship(User user, Faction faction) {
        return getFactionRelationship(user.getFaction().orElse(null), faction);
    }

    public void setFactionRelationship(Faction a, Faction b, RelationshipType relationshipType) {
        Optional<FactionRelationship> factionRelationshipA = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(a, b));
        if (factionRelationshipA.isPresent()) {
            factionRelationshipA.get().setRelationshipType(relationshipType);
            return;
        }
        Optional<FactionRelationship> factionRelationshipB = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(b, a));
        if (factionRelationshipB.isPresent()) {
            factionRelationshipB.get().setRelationshipType(relationshipType);
            return;
        }
        FactionRelationship factionRelationship = new FactionRelationship(a, b, relationshipType);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().addEntry(factionRelationship);
    }

    public FactionRank getFactionRank(User user, Faction faction) {
        switch (getFactionRelationship(user, faction)) {
            case ALLY:
                return FactionRank.ALLY;
            case TRUCE:
                return FactionRank.TRUCE;
            case ENEMY:
                return FactionRank.ENEMY;
            default:
                return user.getFactionRank();
        }
    }

    public Optional<FactionRelationshipRequest> getFactionRelationshipRequest(Faction faction1, Faction faction2, RelationshipType relationshipType) {
        Optional<FactionRelationshipRequest> factionRelationshipRequestA = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().getEntry(new FactionRelationshipRequest(faction1, faction2, relationshipType, new User(UUID.randomUUID(), "")));
        if (factionRelationshipRequestA.isPresent()) {
            return factionRelationshipRequestA;
        }
        return IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().getEntry(new FactionRelationshipRequest(faction2, faction1, relationshipType, new User(UUID.randomUUID(), "")));
    }

    public FactionRelationShipRequestResponse sendFactionRelationshipRequest(User user, Faction faction, RelationshipType newRelationship) {
        Faction userFaction = user.getFaction().orElse(null);
        if (userFaction == null) {
            throw new UnsupportedOperationException("The user's faction cannot be null");
        }
        RelationshipType relationshipType = getFactionRelationship(user, faction);
        if (relationshipType == newRelationship) return FactionRelationShipRequestResponse.SAME_RELATIONSHIP;
        if (newRelationship.getRank() < relationshipType.getRank()) {
            setFactionRelationship(userFaction, faction, newRelationship);
            return FactionRelationShipRequestResponse.SET;
        }
        Optional<FactionRelationshipRequest> factionRelationshipRequest = getFactionRelationshipRequest(userFaction, faction, newRelationship);
        if (factionRelationshipRequest.isPresent()) {
            factionRelationshipRequest.get().accept(user);
            return FactionRelationShipRequestResponse.REQUEST_ACCEPTED;
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(userFaction, faction, newRelationship, user));
        return FactionRelationShipRequestResponse.REQUEST_SENT;
    }

    public CompletableFuture<Double> getFactionValue(@NotNull Faction faction) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();
            double total = 0.00;
            faction.getBlockCountCache().clear();
            faction.getSpawnerCountCache().clear();
            for (Chunk chunk : getFactionChunks(faction).join()) {
                ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(true, false, false);
                World world = chunk.getWorld();
                int maxHeight = world.getMaxHeight() - 1;
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        final int maxy = Math.min(maxHeight, chunkSnapshot.getHighestBlockYAt(x, z));
                        for (int y = LocationUtils.getMinHeight(world); y <= maxy; y++) {
                            XMaterial material = XMaterial.matchXMaterial(chunkSnapshot.getBlockType(x, y, z));
                            if (material.equals(XMaterial.AIR)) continue;
                            total += IridiumFactions.getInstance().getBlockValues().blockValues.getOrDefault(material, new BlockValues.ValuableBlock(0, "", 0, 0)).value;
                            faction.getBlockCountCache().put(material, faction.getBlockCountCache().getOrDefault(material, 0) + 1);
                        }
                    }
                }

                for (BlockState blockState : chunk.getTileEntities()) {
                    if (!(blockState instanceof CreatureSpawner)) continue;
                    CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
                    total += IridiumFactions.getInstance().getBlockValues().spawnerValues.getOrDefault(creatureSpawner.getSpawnedType(), new BlockValues.ValuableBlock(0, "", 0, 0)).value;
                    faction.getSpawnerCountCache().put(creatureSpawner.getSpawnedType(), faction.getSpawnerCountCache().getOrDefault(creatureSpawner.getSpawnedType(), 0) + 1);
                }
            }
            IridiumFactions.getInstance().getLogger().info("Finished Calculating Faction Value, took " + (System.nanoTime() - startTime) / 1000000 + "ms");
            return total;
        });
    }

    public CompletableFuture<List<Chunk>> getFactionChunks(@NotNull Faction faction) {
        return CompletableFuture.supplyAsync(() ->
                IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).stream()
                        .map(FactionClaim::getChunk)
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a list of all Factions sorted by SortType
     *
     * @param sortType How we are sorting the Factions
     * @return The sorted list of all Factions
     */
    public CompletableFuture<List<Faction>> getFactions(SortType sortType) {
        return CompletableFuture.supplyAsync(() -> {
            if (sortType == SortType.VALUE) {
                return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries().stream()
                        .sorted(Comparator.<Faction, Double>comparing(faction -> faction.getValue().join()).reversed())
                        .collect(Collectors.toList());
            }
            return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries();
        });
    }

    /**
     * Gets a list of all Factions
     *
     * @return The list of all Factions
     */
    public List<Faction> getFactions() {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries();
    }

    /**
     * Represents a way of ordering Islands.
     */
    public enum SortType {
        VALUE
    }

    public List<FactionInvite> getFactionInvites(@NotNull Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().getEntries().stream()
                .filter(factionInvite -> factionInvite.getFactionID() == faction.getId())
                .collect(Collectors.toList());
    }

    public List<User> getFactionMembers(@NotNull Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().getEntries().stream().filter(user -> user.getFactionID() == faction.getId()).collect(Collectors.toList());
    }

    public Optional<FactionInvite> getFactionInvite(@NotNull Faction faction, @NotNull User user) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().getEntry(new FactionInvite(faction, user, user));
    }
}
