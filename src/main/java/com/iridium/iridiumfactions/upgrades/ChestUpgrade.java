package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class ChestUpgrade extends UpgradeData {
    public int rows;

    public ChestUpgrade(int cost, int rows) {
        super(cost);
        this.rows = rows;
    }

    @Override
    public List<Placeholder> getPlaceholders() {
        return Arrays.asList(
                new Placeholder("rows", String.valueOf(rows)),
                new Placeholder("slots", String.valueOf(rows * 9))
        );
    }
}
