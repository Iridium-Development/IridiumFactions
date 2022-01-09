package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class ExperienceUpgrade extends UpgradeData {
    public double experienceModifier;

    public ExperienceUpgrade(int cost, double experienceModifier) {
        super(cost);
        this.experienceModifier = experienceModifier;
    }

    @Override
    public List<Placeholder> getPlaceholders() {
        return Collections.singletonList(new Placeholder("modifier", String.valueOf(experienceModifier)));
    }
}
