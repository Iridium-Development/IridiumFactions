package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.Configuration;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.database.IridiumUser;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class User extends IridiumUser<Faction> {

    @DatabaseField(columnName = "power", canBeNull = false)
    private double power;

    private LocalDateTime lastPowerRegenTime = LocalDateTime.now();

    public User(UUID uuid, String name) {
        setUuid(uuid);
        setName(name);
        setJoinTime(LocalDateTime.now());
        this.power = IridiumFactions.getInstance().getConfiguration().startingPower;
    }

    public Faction getFaction() {
        return IridiumFactions.getInstance().getTeamManager().getFactionViaID(getTeamID());
    }

    @Override
    public void bukkitTask(IridiumTeams<Faction, ?> iridiumTeams) {
        super.bukkitTask(iridiumTeams);
        regenPower();
    }

    private void regenPower() {
        Configuration configuration = IridiumFactions.getInstance().getConfiguration();
        LocalDateTime now = LocalDateTime.now();
        if (!lastPowerRegenTime.plusSeconds(configuration.powerRecoveryDelayInSeconds).isBefore(now)) {
            return;
        }
        lastPowerRegenTime = now;
        setPower(Math.min(power + configuration.powerRecoveryAmount, configuration.maxPower));

    }
}
