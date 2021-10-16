package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
        if (getFactionViaChunk(chunk).isPresent()) return;
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, chunk));
        player.sendMessage("Claimed land for your faction");
    }

    public void claimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
                claimFactionLand(faction, chunk, player);
            }
        }
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
