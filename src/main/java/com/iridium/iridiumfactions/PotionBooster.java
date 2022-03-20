package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XPotion;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PotionBooster extends Booster {

    public int strength;
    public XPotion xPotion;

    public PotionBooster(Item item, int cost, int time, String name, boolean stackable, boolean enabled, int strength, XPotion xPotion) {
        super(item, cost, time, name, stackable, enabled);
        this.strength = strength;
        this.xPotion = xPotion;
    }
}
