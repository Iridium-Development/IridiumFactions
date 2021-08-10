package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    @DatabaseField(columnName = "faction_id", canBeNull = false)
    private int factionID;

    @DatabaseField(columnName = "faction_rank", canBeNull = false)
    private @NotNull FactionRank factionRank;

    /**
     * The default constructor.
     *
     * @param uuid The UUID of the {@link Player}
     * @param name The name of the Player
     */
    public User(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
        this.factionRank = FactionRank.VISITOR;
    }

    public Optional<Faction> getFaction() {
        return IridiumFactions.getInstance().getFactionManager().getFactionById(factionID);
    }

}
