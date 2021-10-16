package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_claims")
public class FactionClaim extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "x", canBeNull = false)
    private int x;

    @DatabaseField(columnName = "z", canBeNull = false)
    private int z;

    @DatabaseField(columnName = "world", canBeNull = false)
    private @NotNull String world;

    public FactionClaim(@NotNull Faction faction, @NotNull Chunk chunk) {
        super(faction);
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld().getName();
    }

    public FactionClaim(@NotNull Faction faction, @NotNull String world, int x, int z) {
        super(faction);
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public Chunk getChunk() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) world = Bukkit.getWorlds().get(0);
        return world.getChunkAt(x, z);
    }
}
