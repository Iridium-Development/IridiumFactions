package com.iridium.iridiumfactions.database;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.configs.BlockValues;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

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

    @Setter(AccessLevel.NONE)
    private FactionType factionType = FactionType.PLAYER_FACTION;

    private Cache<Double> valueCache = new Cache<>(500);

    /**
     * Constructor used for Wilderness Warzone and Safezone
     *
     * @param factionType The type of faction this is
     */
    public Faction(@NotNull FactionType factionType) {
        this.factionType = factionType;
        switch (factionType) {
            case WILDERNESS:
                this.id = -1;
                this.name = IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultName;
                this.description = IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultDescription;
                break;
            case WARZONE:
                this.id = -2;
                this.name = IridiumFactions.getInstance().getConfiguration().warzoneFaction.defaultName;
                this.description = IridiumFactions.getInstance().getConfiguration().warzoneFaction.defaultDescription;
                break;
            case SAFEZONE:
                this.id = -3;
                this.name = IridiumFactions.getInstance().getConfiguration().safezoneFaction.defaultName;
                this.description = IridiumFactions.getInstance().getConfiguration().safezoneFaction.defaultDescription;
                break;
        }
    }

    /**
     * The default constructor.
     *
     * @param name The name of the Faction
     */
    public Faction(final @NotNull String name) {
        this.name = name;
        this.description = IridiumFactions.getInstance().getConfiguration().playerFaction.defaultDescription;
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public User getOwner() {
        return IridiumFactions.getInstance().getFactionManager().getFactionMembers(this).stream().filter(user ->
                user.getFactionRank().equals(FactionRank.OWNER)
        ).findFirst().orElse(new User(UUID.randomUUID(), ""));
    }

    /**
     * The constructor used for testing.
     *
     * @param name The name of the Faction
     * @param id   The id of the Faction
     */
    public Faction(final @NotNull String name, int id) {
        this.id = id;
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
        return IridiumFactions.getInstance().getFactionManager().getFactionMembers(this).stream().map(User::getPower).reduce(0.00, Double::sum) + getExtraPower();
    }

    public int getExtraPower() {
        return IridiumFactions.getInstance().getUpgrades().powerUpgrade.upgrades.get(IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(this, UpgradeType.POWER_UPGRADE).getLevel()).extraPower;
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

    public double getValue() {
        return valueCache.getCache(() -> {
            double totalValue = 0.00;

            for (Map.Entry<XMaterial, BlockValues.ValuableBlock> valuableBlocks : IridiumFactions.getInstance().getBlockValues().blockValues.entrySet()) {
                totalValue += IridiumFactions.getInstance().getFactionManager().getFactionBlockAmount(this, valuableBlocks.getKey()) * valuableBlocks.getValue().value;
            }

            for (Map.Entry<EntityType, BlockValues.ValuableBlock> valuableSpawners : IridiumFactions.getInstance().getBlockValues().spawnerValues.entrySet()) {
                totalValue += IridiumFactions.getInstance().getFactionManager().getFactionSpawnerAmount(this, valuableSpawners.getKey()) * valuableSpawners.getValue().value;
            }

            double strikeReductions = 1 - getStrikeReductions() / 100.00;

            return totalValue * strikeReductions;
        });
    }

    public int getStrikeReductions() {
        return IntStream.rangeClosed(0, IridiumFactions.getInstance().getFactionManager().getFactionStrikes(this).size())
                .map(strikes -> IridiumFactions.getInstance().getConfiguration().factionStrikesValueReductionPercent.getOrDefault(strikes, 0))
                .max().orElse(0);
    }

    public int getRank() {
        return IridiumFactions.getInstance().getFactionManager().getFactions(FactionManager.SortType.VALUE).indexOf(this) + 1;
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
