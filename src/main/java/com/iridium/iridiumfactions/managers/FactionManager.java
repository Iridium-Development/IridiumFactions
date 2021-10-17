package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
            Faction faction = IridiumFactions.getInstance().getDatabaseManager().registerFaction(new Faction(name)).join();

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
            if (faction.getRemainingPower() < 1) {
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
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, world.getName(), x, z));
        });
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            World world = centerChunk.getWorld();
            for (int x = centerChunk.getX() - (radius - 1); x <= centerChunk.getX() + (radius - 1); x++) {
                for (int z = centerChunk.getZ() - (radius - 1); z <= centerChunk.getZ() + (radius - 1); z++) {
                    if (faction.getRemainingPower() < 1) {
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                        getFactionMembers(faction).forEach(user -> {
                            Player p = user.getPlayer();
                            if (p != null) {
                                p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().ranOutOfPowerWhileClaimingLand
                                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                        .replace("%player%", player.getName())
                                        .replace("%faction%", faction.getName())
                                        .replace("%size%", String.valueOf(((radius - 1) * 2) + 1))
                                        .replace("%ms%", String.valueOf(duration))
                                ));
                            }
                        });
                        return;
                    }
                    claimFactionLand(faction, world, x, z, player).join();
                }
            }
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
            getFactionMembers(faction).forEach(user -> {
                Player p = user.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", player.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%size%", String.valueOf(((radius - 1) * 2) + 1))
                            .replace("%ms%", String.valueOf(duration))
                    ));
                }
            });
        });
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, Chunk chunk, Player player) {
        return unClaimFactionLand(faction, chunk.getWorld(), chunk.getX(), chunk.getZ(), player);
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, World world, int x, int z, Player player) {
        return CompletableFuture.runAsync(() -> {
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
            getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                    member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", player.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%size%", "1")
                    ))
            );
        });
    }

    public CompletableFuture<Void> unClaimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        return CompletableFuture.runAsync(() -> {
            for (FactionClaim factionClaim : IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction)) {
                if (factionClaim.getX() > centerChunk.getX() - radius && factionClaim.getX() < centerChunk.getX() + radius) {
                    if (factionClaim.getZ() > centerChunk.getZ() - radius && factionClaim.getZ() < centerChunk.getZ() + radius) {
                        if (factionClaim.getWorld().equals(centerChunk.getWorld().getName())) {
                            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(factionClaim).join();
                        }
                    }
                }
            }
            getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                    member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", player.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%size%", String.valueOf(((radius - 1) * 2) + 1))
                    ))
            );
        });
    }

    public CompletableFuture<Void> unClaimAllFactionLand(Faction faction, Player player) {
        return CompletableFuture.runAsync(() -> {
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
