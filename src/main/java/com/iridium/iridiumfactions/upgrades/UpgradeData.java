package com.iridium.iridiumfactions.upgrades;

import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.utils.Placeholder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UpgradeData {
    public int money;

    @JsonIgnore
    public List<Placeholder> getPlaceholders() {
        return Collections.emptyList();
    }
}
