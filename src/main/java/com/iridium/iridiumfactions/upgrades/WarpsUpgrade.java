package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class WarpsUpgrade extends UpgradeData {
    public int warps;

    public WarpsUpgrade(int cost, int warps) {
        super(cost);
        this.warps = warps;
    }

    @Override
    public List<Placeholder> getPlaceholders() {
        return Collections.singletonList(new Placeholder("warps", String.valueOf(warps)));
    }
}
