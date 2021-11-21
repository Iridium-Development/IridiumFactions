package com.iridium.iridiumfactions.configs.inventories;

import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FactionTopInventoryConfig extends NoItemGUI{
    /**
     * The item for top islands
     */
    public Item item;
    /**
     * The filler item if there isnt a # top island
     */
    public Item filler;

    public FactionTopInventoryConfig(int size, String title, Background background, Item item, Item filler) {
        this.size = size;
        this.title = title;
        this.background = background;
        this.item = item;
        this.filler = filler;
    }
}
