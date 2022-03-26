package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_strikes")
public final class FactionStrike extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "reason", canBeNull = false)
    @Setter
    private String reason;

    @DatabaseField(columnName = "user", canBeNull = false)
    private UUID user;

    public FactionStrike(Faction faction, String reason, User user) {
        super(faction);
        this.reason = reason;
        this.user = user.getUuid();
    }

}
