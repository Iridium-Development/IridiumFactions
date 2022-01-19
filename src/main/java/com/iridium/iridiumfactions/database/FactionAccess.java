package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionRank;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_claims")
public class FactionAccess extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "faction_claim_id", canBeNull = false)
    private int claimID;

    @DatabaseField(columnName = "faction_rank", canBeNull = false)
    private FactionRank factionRank;

    @DatabaseField(columnName = "allowed", canBeNull = false)
    @Setter
    private boolean allowed;

    public FactionAccess(@NotNull Faction faction, FactionClaim factionClaim, FactionRank factionRank, boolean allowed) {
        super(faction);
        this.claimID = factionClaim.getId();
        this.factionRank = factionRank;
        this.allowed = allowed;
    }

}
