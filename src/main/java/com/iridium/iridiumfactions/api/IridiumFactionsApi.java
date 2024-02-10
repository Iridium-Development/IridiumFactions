package com.iridium.iridiumfactions.api;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumteams.Permission;
import com.iridium.iridiumteams.PermissionType;
import com.iridium.iridiumteams.bank.BankItem;
import com.iridium.iridiumteams.commands.Command;
import com.iridium.iridiumteams.enhancements.Enhancement;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * General api for IridiumFactions.
 * It is accessible via {@link IridiumFactionsApi#getInstance()}.
 */
public class IridiumFactionsApi {

    private static final IridiumFactionsApi instance;
    private final IridiumFactions iridiumFactions;

    static {
        instance = new IridiumFactionsApi(IridiumFactions.getInstance());
    }

    /**
     * Constructor for api initialization.
     *
     * @param iridiumFactions The instance of the {@link IridiumFactions} class
     */
    private IridiumFactionsApi(@NotNull IridiumFactions iridiumFactions) {
        this.iridiumFactions = iridiumFactions;
    }

    /**
     * Accesses the api instance.
     * Might be null if this method is called when {@link IridiumFactions}'s startup method is still being executed.
     *
     * @return the instance of this api
     * @since 3.0.0
     */
    public static @NotNull IridiumFactionsApi getInstance() {
        return instance;
    }

    /**
     * Adds an Faction BankItem.
     *
     * @param bankItem The specified Bankitem
     * @since 3.0.0
     */
    public void addBankItem(@NotNull BankItem bankItem) {
        iridiumFactions.getBankItemList().add(bankItem);
    }

    /**
     * Adds an Faction enhancement.
     *
     * @param enhancementName The name of the enhancement (used for storage purposes)
     * @param enhancement     the enhancement item
     * @since 4.0.2
     */
    public void addEnhancement(@NotNull String enhancementName, @NotNull Enhancement<?> enhancement) {
        iridiumFactions.addEnhancement(enhancementName, enhancement);
    }

    /**
     * Adds an Faction permission.
     *
     * @param permission The specified Permission
     * @param key        the unique key associated with this permission
     * @since 3.0.0
     */
    public void addPermission(@NotNull Permission permission, @NotNull String key) {
        iridiumFactions.getPermissionList().put(key, permission);
    }

    /**
     * Adds an IridiumFactions command.
     *
     * @param command The command that should be added
     * @since 3.0.0
     */
    public void addCommand(@NotNull Command<Faction, User> command) {
        iridiumFactions.getCommandManager().registerCommand(command);
    }

    /**
     * Gets a {@link User}'s info. Creates one if they don't exist.
     *
     * @param offlinePlayer The player who's data should be fetched
     * @return the user data
     * @since 3.0.0
     */
    public @NotNull User getUser(@NotNull OfflinePlayer offlinePlayer) {
        return iridiumFactions.getUserManager().getUser(offlinePlayer);
    }

    /**
     * Finds an Faction by its id.
     *
     * @param id The id of the Faction
     * @return Optional with the Faction, empty if there is none
     * @since 3.0.0
     */
    public @NotNull Optional<Faction> getFactionById(int id) {
        return iridiumFactions.getFactionManager().getTeamViaID(id);
    }

    /**
     * Finds an Faction by its name.
     *
     * @param name The name of the Faction
     * @return Optional with the Faction, empty if there is none
     * @since 3.0.0
     */
    public @NotNull Optional<Faction> getFactionByName(@NotNull String name) {
        return iridiumFactions.getFactionManager().getTeamViaName(name);
    }

    /**
     * Gets an {@link Faction} from a location.
     *
     * @param location The location you are looking at
     * @return Optional of the Faction at the location, empty if there is none
     * @since 3.0.0
     */
    public @NotNull Optional<Faction> getFactionViaLocation(@NotNull Location location) {
        return iridiumFactions.getFactionManager().getTeamViaLocation(location);
    }

    /**
     * Gets a permission object from name.
     *
     * @param permissionKey The permission key
     * @return the permission
     * @since 3.0.0
     */
    public @NotNull Optional<Permission> getPermissions(@NotNull String permissionKey) {
        return Optional.ofNullable(iridiumFactions.getPermissionList().get(permissionKey));
    }

    /**
     * Gets a permission object from name.
     *
     * @param permissionType The permission key
     * @return the permission
     * @since 3.0.4
     */
    public @NotNull Optional<Permission> getPermissions(@NotNull PermissionType permissionType) {
        return getPermissions(permissionType.getPermissionKey());
    }

    /**
     * Gets whether a permission is allowed or denied.
     *
     * @param faction     The specified Faction
     * @param user       The Specified user
     * @param permission The Specified permission
     * @param key        The permission key
     * @return true if the permission is allowed
     * @since 3.0.0
     */
    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull Permission permission, @NotNull String key) {
        return iridiumFactions.getFactionManager().getTeamPermission(faction, user, key);
    }

    /**
     * Gets whether a permission is allowed or denied.
     *
     * @param faction     The specified Faction
     * @param user       The specified user
     * @param permissionType The specified permission type
     * @return true if the permission is allowed
     * @since 3.0.4
     */
    public boolean getFactionPermission(@NotNull Faction faction, @NotNull User user, @NotNull PermissionType permissionType) {
        return iridiumFactions.getFactionManager().getTeamPermission(faction, user, permissionType);
    }

    /**
     * Gets a list of Factions sorted by SortType.
     *
     * @param sortType How we are sorting the Factions
     * @return sorted list of all Factions
     * @since 3.0.0
     */
    public @NotNull List<Faction> getFactions(@NotNull FactionManager.SortType sortType) {
        return iridiumFactions.getFactionManager().getTeams(sortType, false);
    }

}
