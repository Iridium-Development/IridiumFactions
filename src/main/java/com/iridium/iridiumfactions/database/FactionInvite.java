package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_invites")
public class FactionInvite extends FactionData{

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "user", canBeNull = false)
    private @NotNull UUID user;

    @DatabaseField(columnName = "inviter", canBeNull = false)
    private @NotNull UUID inviter;

    @DatabaseField(columnName = "time", canBeNull = false)
    private long time;

    public FactionInvite(@NotNull Faction faction, @NotNull User user, @NotNull User inviter) {
        super(faction);
        this.user = user.getUuid();
        this.inviter = inviter.getUuid();
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }
}
