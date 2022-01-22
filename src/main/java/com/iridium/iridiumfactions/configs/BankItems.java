package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.bank.ExperienceBankItem;
import com.iridium.iridiumfactions.bank.MoneyBankItem;
import com.iridium.iridiumfactions.bank.TnTBankItem;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankItems {

    public ExperienceBankItem experienceBankItem = new ExperienceBankItem(10, new Item(XMaterial.EXPERIENCE_BOTTLE, 15, 1, "&c&lFaction Experience", Arrays.asList("&7%amount% Experience", "&c&l[!] &cLeft click to withdraw", "&c&l[!] &cRight click to deposit")));
    public MoneyBankItem moneyBankItem = new MoneyBankItem(10, new Item(XMaterial.PAPER, 11, 1, "&c&lFaction Money", Arrays.asList("&7$%amount%", "&c&l[!] &cLeft click to withdraw", "&c&l[!] &cRight click to deposit")));
    public TnTBankItem tnTBankItem = new TnTBankItem(64, new Item(XMaterial.TNT, 13, 1, "&c&lFaction TnT", Arrays.asList("&7%amount% TnT", "&c&l[!] &cLeft click to withdraw", "&c&l[!] &cRight click to deposit")));
}
