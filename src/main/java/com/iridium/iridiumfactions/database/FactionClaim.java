package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumteams.database.DatabaseObject;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Getter
@NoArgsConstructor
public class FactionClaim extends DatabaseObject {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "x", canBeNull = false)
    private int x;

    @DatabaseField(columnName = "z", canBeNull = false)
    private int z;

    @DatabaseField(columnName = "world", canBeNull = false)
    private @NotNull String world;


    @DatabaseField(columnName = "team_id", canBeNull = false)
    @Setter
    private int teamID;

    public FactionClaim(@NotNull Faction faction, @NotNull Chunk chunk) {
        setFaction(faction);
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld().getName();
    }

    public FactionClaim(@NotNull Faction faction, @NotNull String world, int x, int z) {
        setFaction(faction);
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public CompletableFuture<Chunk> getChunk() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) world = Bukkit.getWorlds().get(0);
        return IridiumFactions.getInstance().getMultiVersion().getChunkAt(world, x, z);
    }

    public Faction getFaction(){
        return IridiumFactions.getInstance().getFactionManager().getTeamViaID(getTeamID()).orElse(new Faction(FactionType.WILDERNESS));
    }

    public void setFaction(Faction faction){
        setTeamID(faction.getId());
    }
}
