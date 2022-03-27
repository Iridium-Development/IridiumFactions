package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.bank.BankItem;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FactionManager {

    @NotNull
    public Faction getFactionViaId(int id) {
        switch (id) {
            case -1:
                return new Faction(FactionType.WILDERNESS);
            case -2:
                return new Faction(FactionType.WARZONE);
            case -3:
                return new Faction(FactionType.SAFEZONE);
            default:
                return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id).orElse(getFactionViaId(-1));
        }
    }

    public List<FactionWarp> getFactionWarps(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction);
    }

    public Optional<FactionWarp> getFactionWarp(Faction faction, String name) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntry(new FactionWarp(faction, null, name));
    }

    public Optional<Faction> getFactionViaName(String name) {
        switch (name.toUpperCase()) {
            case "WILDERNESS":
                return Optional.of(new Faction(FactionType.WILDERNESS));
            case "WARZONE":
                return Optional.of(new Faction(FactionType.WARZONE));
            case "SAFEZONE":
                return Optional.of(new Faction(FactionType.SAFEZONE));
            default:
                return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(name);
        }
    }

    public Optional<Faction> getFactionViaNameOrPlayer(String name) {
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(name);
        Faction playerFaction = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer).getFaction();
        if (playerFaction.getFactionType() == FactionType.WILDERNESS) {
            return getFactionViaName(name);
        }
        return Optional.of(playerFaction);
    }

    @NotNull
    public Faction getFactionViaLocation(Location location) {
        return getFactionViaChunk(location.getChunk());
    }

    @NotNull
    public Faction getFactionViaChunk(Chunk chunk) {
        return getFactionViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    @NotNull
    public Faction getFactionViaChunk(World world, int x, int z) {
        return getFactionViaId(getFactionClaimViaChunk(world, x, z).map(FactionData::getFactionID).orElse(-1));
    }

    public Optional<FactionClaim> getFactionClaimViaChunk(Chunk chunk) {
        return getFactionClaimViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public Optional<FactionClaim> getFactionClaimViaChunk(World world, int x, int z) {
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
            Optional<FactionClaim> factionClaim = getFactionClaimViaChunk(world, x, z);
            if (factionClaim.isPresent() && !user.isBypassing() && factionClaim.get().getFaction().getRemainingPower() >= 0) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%faction%", factionClaim.get().getFaction().getName())
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
            if (user.getFactionID() != faction.getId()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", user.getName())
                        .replace("%faction%", faction.getName())
                        .replace("%x%", String.valueOf(x))
                        .replace("%z%", String.valueOf(z))
                ));
            }
            if (factionClaim.isPresent()) {
                if (faction.getFactionType() == FactionType.WILDERNESS) {
                    IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(factionClaim.get());
                } else {
                    factionClaim.get().setFaction(faction);
                }
            } else {
                IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, world.getName(), x, z));
            }
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
            Faction factionClaimedAtLand = getFactionViaId(factionClaim.map(FactionData::getFactionID).orElse(-1));
            if (!factionClaim.isPresent() || factionClaimedAtLand.getId() != faction.getId()) {
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

    public List<FactionStrike> getFactionStrikes(@NotNull Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionStrikeTableManager().getEntries(faction);
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull FactionRank factionRank, @NotNull Permission permission, @NotNull String key) {
        if (faction.getFactionType() == FactionType.WILDERNESS) return true;
        if (faction.getFactionType() != FactionType.PLAYER_FACTION) return false;
        Optional<FactionPermission> factionPermission = IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, key, factionRank, true));
        return factionPermission.map(FactionPermission::isAllowed).orElseGet(() -> factionRank.getLevel() >= permission.getDefaultRank().getLevel());
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull Permission permission, @NotNull String key) {
        return user.isBypassing() || getFactionPermission(faction, getFactionRank(user, faction), permission, key);
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull PermissionType permissionType) {
        return getFactionPermission(faction, user, IridiumFactions.getInstance().getPermissionList().get(permissionType.getPermissionKey()), permissionType.getPermissionKey());
    }

    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull PermissionType permissionType, @NotNull Location location) {
        Optional<FactionClaim> factionClaim = getFactionClaimViaChunk(location.getChunk());
        if (factionClaim.isPresent()) {
            Optional<FactionAccess> factionAccess = IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().getEntry(new FactionAccess(faction, factionClaim.get(), user.getFactionRank(), true));
            if (!factionAccess.map(FactionAccess::isAllowed).orElse(true)) return false;
        }
        return getFactionPermission(faction, user, permissionType);
    }

    public Inventory getFactionChestInventory(Faction faction, int page) {
        FactionChest factionChest = getFactionChest(faction, page);
        FactionUpgrade factionUpgrade = getFactionUpgrade(faction, UpgradeType.CHEST_UPGRADE);
        int maxSlots = IridiumFactions.getInstance().getUpgrades().chestUpgrade.upgrades.get(factionUpgrade.getLevel()).rows * 9;
        int inventorySize = Math.min(54, maxSlots - ((page - 1) * 54));
        if (factionChest.getFactionChest() != null && factionChest.getFactionChest().getSize() == inventorySize) {
            return factionChest.getFactionChest();
        } else {
            Inventory inventory = Bukkit.createInventory(null, inventorySize, StringUtils.color(IridiumFactions.getInstance().getConfiguration().factionChestTitle.replace("%faction_name%", faction.getName())));
            Inventory oldInventory = factionChest.getFactionChest();
            if (oldInventory != null) {
                inventory.setContents(oldInventory.getContents());
                oldInventory.clear();
                for (HumanEntity humanEntity : new ArrayList<>(oldInventory.getViewers())) {
                    humanEntity.openInventory(inventory);
                }
            }
            factionChest.setFactionChest(inventory);
            return inventory;
        }
    }

    private synchronized FactionChest getFactionChest(Faction faction, int page) {
        Optional<FactionChest> factionChest = IridiumFactions.getInstance().getDatabaseManager().getFactionChestTableManager().getEntry(new FactionChest(faction, null, page));
        if (factionChest.isPresent()) {
            return factionChest.get();
        } else {
            FactionChest chest = new FactionChest(faction, null, page);
            IridiumFactions.getInstance().getDatabaseManager().getFactionChestTableManager().addEntry(chest);
            return chest;
        }
    }

    public synchronized void setFactionPermission(@NotNull Faction faction, @NotNull FactionRank factionRank, @NotNull String key, boolean allowed) {
        Optional<FactionPermission> factionPermission = IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().getEntry(new FactionPermission(faction, key, factionRank, allowed));
        if (factionPermission.isPresent()) {
            factionPermission.get().setAllowed(allowed);
        } else {
            IridiumFactions.getInstance().getDatabaseManager().getFactionPermissionTableManager().addEntry(new FactionPermission(faction, key, factionRank, allowed));
        }
    }

    public synchronized void setFactionAccess(Faction faction, FactionRank factionRank, FactionClaim factionClaim, boolean allowed) {
        Optional<FactionAccess> factionAccess = IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().getEntry(new FactionAccess(faction, factionClaim, factionRank, allowed));
        if (factionAccess.isPresent()) {
            factionAccess.get().setAllowed(allowed);
        } else {
            IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().addEntry(new FactionAccess(faction, factionClaim, factionRank, allowed));
        }
    }

    public synchronized FactionBank getFactionBank(Faction faction, BankItem bankItem) {
        Optional<FactionBank> factionBank = IridiumFactions.getInstance().getDatabaseManager().getFactionBankTableManager().getEntry(new FactionBank(faction, bankItem.getName(), 0));
        if (factionBank.isPresent()) {
            return factionBank.get();
        } else {
            FactionBank bank = new FactionBank(faction, bankItem.getName(), 0);
            IridiumFactions.getInstance().getDatabaseManager().getFactionBankTableManager().addEntry(bank);
            return bank;
        }
    }

    public List<Faction> getFactionRelationships(Faction faction, RelationshipType relationshipType) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntries().stream()
                .filter(factionRelationship -> factionRelationship.getFactionID() == faction.getId() || factionRelationship.getFaction2ID() == faction.getId())
                .filter(factionRelationshipEntry -> factionRelationshipEntry.getRelationshipType() == relationshipType)
                .map(relationship -> faction.getId() == relationship.getFactionID() ? relationship.getFaction2ID() : relationship.getFactionID())
                .map(this::getFactionViaId)
                .collect(Collectors.toList());
    }

    public RelationshipType getFactionRelationship(@NotNull Faction a, @NotNull Faction b) {
        if (b.getFactionType() == FactionType.WILDERNESS) {
            return RelationshipType.WILDERNESS;
        }
        if (b.getFactionType() == FactionType.WARZONE) {
            return RelationshipType.WARZONE;
        }
        if (b.getFactionType() == FactionType.SAFEZONE) {
            return RelationshipType.SAFEZONE;
        }
        if (a.getFactionType() != FactionType.PLAYER_FACTION) {
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

    public RelationshipType getFactionRelationship(User user, @NotNull Faction faction) {
        return getFactionRelationship(user.getFaction(), faction);
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
        Faction userFaction = user.getFaction();
        RelationshipType relationshipType = getFactionRelationship(user, faction);
        if (relationshipType == newRelationship) return FactionRelationShipRequestResponse.SAME_RELATIONSHIP;
        if (IridiumFactions.getInstance().getConfiguration().factionRelationshipLimits.containsKey(newRelationship)) {
            int limit = IridiumFactions.getInstance().getConfiguration().factionRelationshipLimits.get(newRelationship);
            if (getFactionRelationships(faction, newRelationship).size() >= limit && limit > 0) {
                return FactionRelationShipRequestResponse.THEIR_LIMIT_REACHED;
            }
            if (getFactionRelationships(userFaction, newRelationship).size() >= limit && limit > 0) {
                return FactionRelationShipRequestResponse.YOUR_LIMIT_REACHED;
            }
        }
        if (newRelationship.getRank() < relationshipType.getRank()) {
            setFactionRelationship(userFaction, faction, newRelationship);
            return FactionRelationShipRequestResponse.SET;
        }
        Optional<FactionRelationshipRequest> factionRelationshipRequest = getFactionRelationshipRequest(userFaction, faction, newRelationship);
        if (factionRelationshipRequest.isPresent()) {
            if (user.equals(factionRelationshipRequest.get().getUser().orElse(null))) {
                return FactionRelationShipRequestResponse.ALREADY_SENT_REQUEST;
            }
            factionRelationshipRequest.get().accept(user);
            return FactionRelationShipRequestResponse.REQUEST_ACCEPTED;
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(userFaction, faction, newRelationship, user));
        return FactionRelationShipRequestResponse.REQUEST_SENT;
    }

    /**
     * Gets the FactionBlocks for a specific island and material.
     *
     * @param faction  The specified Faction
     * @param material The specified Material
     * @return The IslandBlock
     */
    public synchronized FactionBlocks getFactionBlock(@NotNull Faction faction, @NotNull XMaterial material) {
        FactionBlocks factionBlocks = new FactionBlocks(faction, material);
        Optional<FactionBlocks> factionBlocksOptional = IridiumFactions.getInstance().getDatabaseManager().getFactionBlocksTableManager().getEntry(factionBlocks);
        if (factionBlocksOptional.isPresent()) {
            return factionBlocksOptional.get();
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionBlocksTableManager().addEntry(factionBlocks);
        return factionBlocks;
    }

    /**
     * Gets the FactionSpawners for a specific island and material.
     *
     * @param faction     The specified Faction
     * @param spawnerType The specified spawner type
     * @return The IslandBlock
     */
    public synchronized FactionSpawners getFactionSpawners(@NotNull Faction faction, @NotNull EntityType spawnerType) {
        FactionSpawners factionSpawners = new FactionSpawners(faction, spawnerType);
        Optional<FactionSpawners> factionSpawnersOptional = IridiumFactions.getInstance().getDatabaseManager().getFactionSpawnersTableManager().getEntry(factionSpawners);
        if (factionSpawnersOptional.isPresent()) {
            return factionSpawnersOptional.get();
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionSpawnersTableManager().addEntry(factionSpawners);
        return factionSpawners;
    }

    public FactionUpgrade getFactionUpgrade(Faction faction, UpgradeType upgradeType) {
        return getFactionUpgrade(faction, upgradeType.getName());
    }

    public synchronized FactionUpgrade getFactionUpgrade(Faction faction, String upgrade) {
        FactionUpgrade factionUpgrade = new FactionUpgrade(faction, upgrade);
        Optional<FactionUpgrade> factionUpgradeOptional = IridiumFactions.getInstance().getDatabaseManager().getFactionUpgradeTableManager().getEntry(factionUpgrade);
        if (factionUpgradeOptional.isPresent()) {
            return factionUpgradeOptional.get();
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionUpgradeTableManager().addEntry(factionUpgrade);
        return factionUpgrade;
    }

    public FactionBooster getFactionBooster(Faction faction, BoosterType boosterType) {
        return getFactionBooster(faction, boosterType.getName());
    }

    public synchronized FactionBooster getFactionBooster(Faction faction, String booster) {
        FactionBooster factionBooster = new FactionBooster(faction, booster);
        Optional<FactionBooster> factionBoosterOptional = IridiumFactions.getInstance().getDatabaseManager().getFactionBoosterTableManager().getEntry(factionBooster);
        if (factionBoosterOptional.isPresent()) {
            return factionBoosterOptional.get();
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionBoosterTableManager().addEntry(factionBooster);
        return factionBooster;
    }

    public int getFactionSpawnerAmount(@NotNull Faction faction, EntityType entityType) {
        return getFactionSpawners(faction, entityType).getAmount();
    }

    public int getFactionBlockAmount(@NotNull Faction faction, XMaterial xMaterial) {
        return getFactionBlock(faction, xMaterial).getAmount();
    }

    public CompletableFuture<Void> recalculateFactionValue(@NotNull Faction faction) {
        return CompletableFuture.runAsync(() -> {
            IridiumFactions.getInstance().getDatabaseManager().getFactionSpawnersTableManager().getEntries(faction).forEach(factionSpawners -> factionSpawners.setAmount(0));
            IridiumFactions.getInstance().getDatabaseManager().getFactionBlocksTableManager().getEntries(faction).forEach(factionBlocks -> factionBlocks.setAmount(0));
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
                            FactionBlocks factionBlocks = getFactionBlock(faction, material);
                            factionBlocks.setAmount(factionBlocks.getAmount() + 1);
                        }
                    }
                }

                for (BlockState blockState : chunk.getTileEntities()) {
                    if (!(blockState instanceof CreatureSpawner)) continue;
                    CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
                    FactionSpawners factionSpawners = getFactionSpawners(faction, creatureSpawner.getSpawnedType());
                    factionSpawners.setAmount(factionSpawners.getAmount() + 1);
                }
            }
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
    public List<Faction> getFactions(SortType sortType) {
        if (sortType == SortType.VALUE) {
            return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries().stream()
                    .sorted(Comparator.comparing(Faction::getValue).reversed())
                    .collect(Collectors.toList());
        }
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries();
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
