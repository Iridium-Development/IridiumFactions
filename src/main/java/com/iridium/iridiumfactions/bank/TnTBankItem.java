package com.iridium.iridiumfactions.bank;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumfactions.database.FactionBank;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the crystals in the island bank.
 * Serialized in the Configuration files.
 */
@NoArgsConstructor
public class TnTBankItem extends BankItem {

    public TnTBankItem(double defaultAmount, Item item) {
        super("tnt", "TnT", defaultAmount, true, item);
    }

    /**
     * Withdraws the given amount of this item from the Player's bank.
     *
     * @param player The player who wants to withdraw
     * @param amount The amount which should be withdrawn
     */
    @Override
    public BankResponse withdraw(Player player, Number amount, FactionBank factionBank) {
        int tnt = Math.min(amount.intValue(), (int) factionBank.getNumber());

        if (tnt > 0) {
            player.getInventory().addItem(new ItemStack(Material.TNT, tnt)).values().forEach(itemStack ->
                    player.getWorld().dropItem(player.getLocation(), itemStack)
            );
            return new BankResponse(tnt, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(tnt, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
    }

    /**
     * Deposits the given amount of this item to the Player's bank.
     *
     * @param player The player who wants to deposit
     * @param amount The amount which should be deposited
     */
    @Override
    public BankResponse deposit(Player player, Number amount, FactionBank factionBank) {
        int tnt = Math.min(amount.intValue(), InventoryUtils.getAmount(player.getInventory(), XMaterial.TNT));
        if (tnt > 0) {
            InventoryUtils.removeAmount(player.getInventory(), XMaterial.TNT, tnt);
            return new BankResponse(tnt, BankResponse.BankResponseType.SUCCESS);
        }
        return new BankResponse(tnt, BankResponse.BankResponseType.INSUFFICIENT_FUNDS);
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
