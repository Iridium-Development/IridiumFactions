package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_boosters")
public final class FactionBooster extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "start_time", canBeNull = false)
    private long time;

    @DatabaseField(columnName = "booster", canBeNull = false, uniqueCombo = true)
    @Setter
    private String booster;

    public FactionBooster(Faction faction, String booster) {
        super(faction);
        this.booster = booster;
        this.time = 0;
    }
    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public void setTime(LocalDateTime time) {
        this.time = ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public boolean isActive() {
        return LocalDateTime.now().until(getTime(), ChronoUnit.SECONDS) > 0;
    }


    public long getRemainingTime() {
        return LocalDateTime.now().until(getTime(), ChronoUnit.SECONDS);
    }

}
