package com.iridium.iridiumfactions.bank;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumfactions.database.FactionBank;
import com.iridium.iridiumfactions.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Represents the experience in the island bank.
 * Serialized in the Configuration files.
 */
@NoArgsConstructor
public class ExperienceBankItem extends BankItem {

    public ExperienceBankItem(double defaultAmount, Item item) {
        super("experience", "Experience", defaultAmount, true, item);
    }

    /**
     * Withdraws the given amount of this item from the Player's bank.
     *
     * @param player The player who wants to withdraw
     * @param amount The amount which should be withdrawn
     */
    @Override
    public BankResponse withdraw(Player player, Number amount, FactionBank factionBank) {
        int experience = Math.min(amount.intValue(), (int) factionBank.getNumber());
        if (experience > 0) {
            PlayerUtils.setTotalExperience(player, PlayerUtils.getTotalExperience(player) + experience);
            return new BankResponse(experience, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(experience, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
    }

    /**
     * Deposits the given amount of this item to the Player's bank.
     *
     * @param player The player who wants to deposit
     * @param amount The amount which should be deposited
     */
    @Override
    public BankResponse deposit(Player player, Number amount, FactionBank factionBank) {
        int experience = Math.min(amount.intValue(), PlayerUtils.getTotalExperience(player));
        if (experience > 0) {
            PlayerUtils.setTotalExperience(player, PlayerUtils.getTotalExperience(player) - experience);
            return new BankResponse(experience, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(experience, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
    }

    /**
     * Returns the string representation of the value of this item.
     *
     * @param number The number which should be formatted
     * @return The string representation of the provided number for this item
     */
    @Override
    public String toString(Number number) {
        return String.valueOf(number.intValue());
    }

}
