package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class UserManager {
    /**
     * Gets a {@link User}'s info. Creates one if he doesn't exist.
     *
     * @param offlinePlayer The player who's data should be fetched
     * @return The user data
     */
    public @NotNull User getUser(@NotNull OfflinePlayer offlinePlayer) {
        Optional<User> userOptional = getUserByUUID(offlinePlayer.getUniqueId());
        if (userOptional.isPresent()) {
            userOptional.get().initBukkitTask();
            return userOptional.get();
        } else {
            Optional<String> name = Optional.ofNullable(offlinePlayer.getName());
            User user = new User(offlinePlayer.getUniqueId(), name.orElse(""));
            IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);
            return user;
        }
    }

    /**
     * Finds an User by his {@link UUID}.
     *
     * @param uuid The uuid of the onlyForPlayers
     * @return the User class of the onlyForPlayers
     */
    public Optional<User> getUserByUUID(@NotNull UUID uuid) {
        return IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().getUser(uuid);
    }
}
