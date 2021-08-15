package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@Getter
public class FactionData {
    @DatabaseField(columnName = "faction_id", canBeNull = false)
    private int factionID;

    public FactionData(Faction faction){
        this.factionID = faction.getId();
    }

    public Optional<Faction> getFaction() {
        return IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID);
    }
}
