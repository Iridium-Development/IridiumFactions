package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class SpawnerUpgrade extends UpgradeData{
    public double spawnerModifier;

    public SpawnerUpgrade(int cost, double spawnerModifier) {
        super(cost);
        this.spawnerModifier = spawnerModifier;
    }

    @Override
    public List<Placeholder> getPlaceholders() {
        return Collections.singletonList(new Placeholder("modifier", String.valueOf(spawnerModifier)));
    }
}
