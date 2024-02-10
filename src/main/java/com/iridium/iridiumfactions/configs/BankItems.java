package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.bankitems.CrystalsBankItem;

import java.util.Arrays;

public class BankItems extends com.iridium.iridiumteams.configs.BankItems {

    public BankItems() {
        super("Faction", "&c");
    }
    
    public CrystalsBankItem crystalsBankItem = new CrystalsBankItem(100, new Item(XMaterial.NETHER_STAR, 13, 1, "&c&lFaction Crystals", Arrays.asList(
            "&7%amount% Crystals",
            "&c&l[!] &cLeft click to withdraw",
            "&c&l[!] &cRight click to deposit")
    ));

}
