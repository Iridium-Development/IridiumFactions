package com.iridium.iridiumfactions.bank;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.FactionBank;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Represents the Vault money in the island bank.
 * Serialized in the Configuration files.
 */
@NoArgsConstructor
public class MoneyBankItem extends BankItem {

    public MoneyBankItem(double defaultAmount, Item item) {
        super("money", "Money", defaultAmount, true, item);
    }

    /**
     * Withdraws the given amount of this item from the Player's bank.
     *
     * @param player The player who wants to withdraw
     * @param amount The amount which should be withdrawn
     */
    @Override
    public BankResponse withdraw(Player player, Number amount, FactionBank factionBank) {
        double money = Math.min(amount.doubleValue(), factionBank.getNumber());
        if (money > 0) {
            IridiumFactions.getInstance().getEconomy().depositPlayer(player, money);
            return new BankResponse(money, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(money, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
    }

    /**
     * Deposits the given amount of this item to the Player's bank.
     *
     * @param player The player who wants to deposit
     * @param amount The amount which should be deposited
     */
    @Override
    public BankResponse deposit(Player player, Number amount, FactionBank factionBank) {
        double money = Math.min(amount.doubleValue(), IridiumFactions.getInstance().getEconomy().getBalance(player));
        if (money > 0) {
            IridiumFactions.getInstance().getEconomy().withdrawPlayer(player, money);
            return new BankResponse(money, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(money, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
    }

    /**
     * Returns the string representation of the value of this item.
     *
     * @param number The number which should be formatted
     * @return The string representation of the provided number for this item
     */
    @Override
    public String toString(Number number) {
        return String.valueOf(number.doubleValue());
    }

}
