package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PowerUpgrade extends UpgradeData {
    public int extraPower;

    public PowerUpgrade(int cost, int extraPower) {
        super(cost);
        this.extraPower = extraPower;
    }

    @Override
    public List<Placeholder> getPlaceholders() {
        return Collections.singletonList(new Placeholder("extraPower", String.valueOf(extraPower)));
    }
}
