package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.Item;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Booster {
    public Item item;
    public int cost;
    public int time;
    public String name;
    public boolean stackable;
    public boolean enabled;
}
