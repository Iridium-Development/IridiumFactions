package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
        int factionID = IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager()
                .getEntry(new FactionClaim(new Faction(""), chunk))
                .map(FactionData::getFactionID)
                .orElse(0);
        return getFactionViaId(factionID);
    }

    private Optional<FactionClaim> getFactionClaimViaChunk(Chunk chunk) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager()
                .getEntry(new FactionClaim(new Faction(""), chunk));
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

    public void claimFactionLand(Faction faction, Chunk chunk, Player player) {
        Optional<Faction> factionClaimedAtLand = getFactionViaChunk(chunk);
        if (factionClaimedAtLand.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%faction%", factionClaimedAtLand.get().getName())
            ));
            return;
        }
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, chunk));
        getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%faction%", faction.getName())
                ))
        );
    }

    public void claimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
                claimFactionLand(faction, chunk, player);
            }
        }
    }

    public void unClaimFactionLand(Faction faction, Chunk chunk, Player player) {
        Optional<FactionClaim> factionClaim = getFactionClaimViaChunk(chunk);
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
                ))
        );
    }

    public void unClaimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        for (FactionClaim factionClaim : IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction)) {
            if (factionClaim.getX() > centerChunk.getX() - radius && factionClaim.getX() < centerChunk.getX() + radius) {
                if (factionClaim.getZ() > centerChunk.getZ() - radius && factionClaim.getZ() < centerChunk.getZ() + radius) {
                    if (factionClaim.getWorld().equals(centerChunk.getWorld().getName())) {
                        unClaimFactionLand(faction, factionClaim.getChunk(), player);
                    }
                }
            }
        }
    }

    public void unClaimAllFactionLand(Faction faction, Player player) {
        for (FactionClaim factionClaim : IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction)) {
            IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().delete(factionClaim);
        }
        getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnClaimedAllLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%faction%", faction.getName())
                ))
        );
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
