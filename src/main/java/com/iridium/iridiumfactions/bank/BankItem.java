package com.iridium.iridiumfactions.bank;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import com.iridium.iridiumfactions.database.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * Represents a type of currency in the bank.
 * Inherit from this class in order to define a new item for the bank.
 */
@NoArgsConstructor
@Getter
public abstract class BankItem {

    private String name;
    @Setter
    private String displayName;
    private double defaultAmount;
    private boolean enabled;
    private Item item;

    public BankItem(String name, String displayName, double defaultAmount, boolean enabled, Item item) {
        this.name = name;
        this.displayName = displayName;
        this.defaultAmount = defaultAmount;
        this.enabled = enabled;
        this.item = item;
    }

    public void withdraw(Player player, Number amount) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = user.getFaction();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, this);
        BankResponse bankResponse = withdraw(player, amount, factionBank);
        if (bankResponse.getBankResponseType() == BankResponse.BankResponseType.SUCCESS) {
            factionBank.setNumber(factionBank.getNumber() - bankResponse.getAmount());
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().bankWithdrew
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%amount%", String.valueOf(bankResponse.getAmount()))
                    .replace("%type%", getDisplayName())
            ));
        } else if (bankResponse.getBankResponseType() == BankResponse.BankResponseType.INSUFFICIENT_FUNDS) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().insufficientFundsToWithdrew
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%amount%", String.valueOf(bankResponse.getAmount()))
                    .replace("%type%", getDisplayName())
            ));
        }
    }

    public abstract BankResponse withdraw(Player player, Number amount, FactionBank factionBank);

    public void deposit(Player player, Number amount) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = user.getFaction();

        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, this);
        BankResponse bankResponse = deposit(player, amount, factionBank);
        if (bankResponse.getBankResponseType() == BankResponse.BankResponseType.SUCCESS) {
            factionBank.setNumber(factionBank.getNumber() + bankResponse.getAmount());
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().bankDeposited
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%amount%", String.valueOf(bankResponse.getAmount()))
                    .replace("%type%", getDisplayName())
            ));
        } else if (bankResponse.getBankResponseType() == BankResponse.BankResponseType.INSUFFICIENT_FUNDS) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().insufficientFundsToDeposit
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%amount%", String.valueOf(bankResponse.getAmount()))
                    .replace("%type%", getDisplayName())
            ));
        }
    }

    public abstract BankResponse deposit(Player player, Number amount, FactionBank factionBank);

    /**
     * Returns the string representation of the value of this item.
     *
     * @param number The number which should be formatted
     * @return The string representation of the provided number for this item
     */
    public abstract String toString(Number number);

}
