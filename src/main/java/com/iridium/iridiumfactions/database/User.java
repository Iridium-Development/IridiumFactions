package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
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

    public User(UUID uuid, String name) {
        setUuid(uuid);
        setName(name);
        setJoinTime(LocalDateTime.now());
        this.power = IridiumFactions.getInstance().getConfiguration().startingPower;
    }

    public Faction getFaction() {
        return IridiumFactions.getInstance().getTeamManager().getFactionViaID(getTeamID());
    }
}
