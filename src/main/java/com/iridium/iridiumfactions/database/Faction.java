package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.Cache;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a Faction of IridiumFactions.
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "factions")
public final class Faction {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", canBeNull = false)
    private @NotNull String name;

    @DatabaseField(columnName = "description", canBeNull = false)
    private @NotNull String description;

    @DatabaseField(columnName = "create_time")
    private long time;

    @DatabaseField(columnName = "home")
    private String home;

    private Cache<Double> valueCache = new Cache<>(500);

    /**
     * The default constructor.
     *
     * @param name The name of the Player
     */
    public Faction(final @NotNull String name) {
        this.name = name;
        this.description = "Default Faction Description";
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * The factions remaining power
     *
     * @return The factions remaining power
     */
    public double getRemainingPower() {
        int land = IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(this).size();
        return getTotalPower() - land;
    }

    /**
     * The factions total power
     *
     * @return The factions total power
     */
    public double getTotalPower() {
        return IridiumFactions.getInstance().getFactionManager().getFactionMembers(this).stream().map(User::getPower).reduce(0.00, Double::sum);
    }

    /**
     * The date this faction was created.
     *
     * @return A LocalDateTime when this faction was created
     */
    public LocalDateTime getCreateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
    }

    public Location getHome() {
        if (this.home == null) return null;
        String[] locations = home.split(",");
        World world = Bukkit.getWorld(locations[0]);
        double x = Double.parseDouble(locations[1]);
        double y = Double.parseDouble(locations[2]);
        double z = Double.parseDouble(locations[3]);
        float pitch = Float.parseFloat(locations[4]);
        float yaw = Float.parseFloat(locations[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setHome(Location location) {
        if (location == null) {
            this.home = null;
        } else {
            String world = location.getWorld() != null ? location.getWorld().getName() : "";
            this.home = world + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getPitch() + "," + location.getYaw();
        }
    }

    public CompletableFuture<Double> getValue() {
        return valueCache.getCacheAsync(() -> IridiumFactions.getInstance().getFactionManager().getFactionValue(this));
    }

    public CompletableFuture<Integer> getRank() {
        return CompletableFuture.supplyAsync(() -> IridiumFactions.getInstance().getFactionManager().getFactions(FactionManager.SortType.VALUE).join().indexOf(this) + 1);
    }

    /**
     * Constructor Used for Comparators
     *
     * @param id The Faction's id
     */
    public Faction(int id) {
        this.id = id;
    }
}
