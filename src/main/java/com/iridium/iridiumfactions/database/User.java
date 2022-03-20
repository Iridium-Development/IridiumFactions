package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.*;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a User of IridiumFactions.
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "users")
public final class User {

    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private @NotNull UUID uuid;

    @DatabaseField(columnName = "name", canBeNull = false)
    private @NotNull String name;

    @Setter(AccessLevel.PRIVATE)
    @DatabaseField(columnName = "faction_id", canBeNull = false)
    private int factionID;

    @DatabaseField(columnName = "join_time")
    private long joinTime;

    @DatabaseField(columnName = "power", canBeNull = false)
    private double power;

    @DatabaseField(columnName = "faction_rank", canBeNull = false)
    private @NotNull FactionRank factionRank;

    private boolean bypassing = false;
    private boolean isFlying = false;

    private FactionChatType factionChatType = FactionChatType.NONE;

    private BukkitTask bukkitTask;

    public User(UUID uuid){
        this.uuid = uuid;
    }

    /**
     * The default constructor.
     *
     * @param uuid The UUID of the {@link Player}
     * @param name The name of the Player
     */
    public User(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinTime = 0L;
        this.factionRank = FactionRank.TRUCE;
        this.power = 10;
    }

    public void initBukkitTask() {
        if (bukkitTask != null) return;
        bukkitTask = Bukkit.getScheduler().runTaskTimer(IridiumFactions.getInstance(), () -> {
            Player player = getPlayer();
            if (player == null) return;
            if (IridiumFactions.getInstance().getBoosters().boostersOnlyInTerritory) {
                Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(player.getLocation());
                if (!IridiumFactions.getInstance().getBoosters().boostersOnlyEffectFactionMembers || faction.getId() == factionID) {
                    applyPotionEffects(faction);
                }
            } else {
                applyPotionEffects(getFaction());
            }
        }, 0, 20);
    }

    public void applyPotionEffects(Faction faction) {
        if (faction.getFactionType() != FactionType.PLAYER_FACTION) return;

        int duration = 10;
        Player player = getPlayer();
        HashMap<PotionEffectType, Integer> potionEffects = new HashMap<>();

        for (Map.Entry<String, Booster> booster : IridiumFactions.getInstance().getBoosterList().entrySet()) {
            if (!(booster.getValue() instanceof PotionBooster)) continue;

            FactionBooster factionBooster = IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, booster.getKey());
            if (!factionBooster.isActive()) continue;
            PotionBooster potionBooster = (PotionBooster) booster.getValue();
            PotionEffectType potionEffectType = potionBooster.xPotion.getPotionEffectType();

            if (!potionEffects.containsKey(potionEffectType)) {
                potionEffects.put(potionEffectType, potionBooster.strength - 1);
            } else if (potionEffects.get(potionEffectType) < potionBooster.strength - 1) {
                potionEffects.put(potionEffectType, potionBooster.strength - 1);
            }
        }

        for (Map.Entry<PotionEffectType, Integer> potionEffectType : potionEffects.entrySet()) {
            Optional<PotionEffect> potionEffect = player.getActivePotionEffects().stream()
                    .filter(effect -> effect.getType().equals(potionEffectType.getKey()))
                    .findFirst();
            if (potionEffect.isPresent()) {
                if (potionEffect.get().getAmplifier() <= potionEffectType.getValue() && potionEffect.get().getDuration() <= duration * 20) {
                    player.removePotionEffect(potionEffectType.getKey());
                }
            }
            player.addPotionEffect(potionEffectType.getKey().createEffect(duration * 20, potionEffectType.getValue()));
        }
    }

    public Faction getFaction() {
        return IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID);
    }

    public void setFaction(Faction faction) {
        this.factionID = faction == null ? 0 : faction.getId();
        setJoinTime(LocalDateTime.now());
        setFactionChatType(FactionChatType.NONE);
    }

    public LocalDateTime getJoinTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(joinTime), ZoneId.systemDefault());
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = ZonedDateTime.of(joinTime, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
