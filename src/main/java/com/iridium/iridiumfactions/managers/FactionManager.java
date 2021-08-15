package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FactionManager {

    public Optional<Faction> getFactionViaId(int id) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id);
    }

    public Optional<Faction> getFactionViaName(String name) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(name);
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

    public Optional<FactionInvite> getFactionInvite(@NotNull Faction faction, @NotNull User user) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().getEntry(new FactionInvite(faction, user, user));
    }

}
