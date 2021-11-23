package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class FactionData {
    @DatabaseField(columnName = "faction_id", canBeNull = false)
    private int factionID;

    public FactionData(Faction faction) {
        this.factionID = faction.getId();
    }

    @NotNull
    public Faction getFaction() {
        return IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID);
    }

    public void setFaction(@NotNull Faction faction) {
        setFactionID(faction.getId());
    }
}
