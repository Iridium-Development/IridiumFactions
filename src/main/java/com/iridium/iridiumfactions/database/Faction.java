package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.Team;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Faction extends Team {

    @Setter(AccessLevel.NONE)
    private FactionType factionType = FactionType.PLAYER_FACTION;

    public Faction(String name) {
        setName(name);
        setDescription(IridiumFactions.getInstance().getConfiguration().defaultDescription);
        setCreateTime(LocalDateTime.now());
    }


    /**
     * Constructor used for Wilderness Warzone and Safezone
     *
     * @param factionType The type of faction this is
     */
    public Faction(@NotNull FactionType factionType) {
        this.factionType = factionType;
        switch (factionType) {
            case WILDERNESS:
                setId(-1);
                setName(IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultName);
                setDescription(IridiumFactions.getInstance().getConfiguration().wildernessFaction.defaultDescription);
                setCreateTime(LocalDateTime.now());
                break;
            case WARZONE:
                setId(-2);
                setName(IridiumFactions.getInstance().getConfiguration().warzoneFaction.defaultName);
                setDescription(IridiumFactions.getInstance().getConfiguration().warzoneFaction.defaultDescription);
                setCreateTime(LocalDateTime.now());
                break;
            case SAFEZONE:
                setId(-3);
                setName(IridiumFactions.getInstance().getConfiguration().safezoneFaction.defaultName);
                setDescription(IridiumFactions.getInstance().getConfiguration().safezoneFaction.defaultDescription);
                setCreateTime(LocalDateTime.now());
                break;
        }
    }

    public Faction(int id) {
        setId(id);
    }

    @Override
    public double getValue() {
        return IridiumFactions.getInstance().getTeamManager().getTeamValue(this);
    }

    @Override
    public @NotNull String getName() {
        if (super.getName() != null) return super.getName();
        String ownerName = IridiumFactions.getInstance().getTeamManager().getTeamMembers(this).stream()
                .filter(user -> user.getUserRank() == Rank.OWNER.getId())
                .findFirst()
                .map(User::getName)
                .orElse("N/A");
        return ownerName + "'s Faction";
    }

    public double getRemainingPower() {
        int land = (int) IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager().getEntries().stream().filter(factionClaim -> factionClaim.getTeamID() == getId()).count();
        return getTotalPower() - land;
    }

    public double getTotalPower() {
        return IridiumFactions.getInstance().getFactionManager().getTeamMembers(this).stream().map(User::getPower).reduce(0.00, Double::sum) + getExtraPower();
    }

    public int getExtraPower() {
        return 0;
//        return IridiumFactions.getInstance().getUpgrades().powerUpgrade.upgrades.get(IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(this, UpgradeType.POWER_UPGRADE).getLevel()).extraPower;
    }
}
