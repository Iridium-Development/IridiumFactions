package com.iridium.iridiumfactions.configs.inventories;

import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FactionRanksInventoryConfig extends NoItemGUI {
    /**
     * The item for Owner Rank
     */
    public Item owner;
    /**
     * The item for Co-Owner Rank
     */
    public Item coOwner;
    /**
     * The item for Moderator Rank
     */
    public Item moderator;
    /**
     * The item for Member Rank
     */
    public Item member;
    /**
     * The item for Truce Rank
     */
    public Item truce;
    /**
     * The item for Ally Rank
     */
    public Item ally;
    /**
     * The item for Enemy Rank
     */
    public Item enemy;

    public FactionRanksInventoryConfig(int size, String title, Background background, Item owner, Item coOwner, Item moderator, Item member, Item truce, Item ally, Item enemy) {
        this.size = size;
        this.title = title;
        this.background = background;
        this.owner = owner;
        this.coOwner = coOwner;
        this.moderator = moderator;
        this.member = member;
        this.truce = truce;
        this.ally = ally;
        this.enemy = enemy;
    }

}