package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Faction extends Team {
    public Faction(String name) {
        setName(name);
        setDescription(IridiumFactions.getInstance().getConfiguration().defaultDescription);
        setCreateTime(LocalDateTime.now());
    }

    public Faction(int id) {
        setId(id);
    }

    @Override
    public double getValue() {
        return IridiumFactions.getInstance().getTeamManager().getTeamValue(this);
    }

    @Override
    public @NotNull String getName() {
        if (super.getName() != null) return super.getName();
        String ownerName = IridiumFactions.getInstance().getTeamManager().getTeamMembers(this).stream()
                .filter(user -> user.getUserRank() == Rank.OWNER.getId())
                .findFirst()
                .map(User::getName)
                .orElse("N/A");
        return ownerName + "'s Faction";
    }
}
