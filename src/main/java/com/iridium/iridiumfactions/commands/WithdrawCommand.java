package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.bank.BankItem;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WithdrawCommand extends Command {

    /**
     * The default constructor.
     */
    public WithdrawCommand() {
        super(Collections.singletonList("withdraw"), "withdraw from your Island bank", "%prefix% &7/is withdraw <name> <amount>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 3) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<BankItem> bankItem = IridiumFactions.getInstance().getBankItemList().stream().filter(item -> item.getName().equalsIgnoreCase(args[1])).findFirst();
        if (!bankItem.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noSuchBankItem.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        try {
            bankItem.get().withdraw(player, Double.parseDouble(args[2]));
            return true;
        } catch (NumberFormatException exception) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
    }

    /**
     * Handles tab-completion for this command.
     *
     * @param commandSender The CommandSender which tries to tab-complete
     * @param command       The command
     * @param label         The label of the command
     * @param args          The arguments already provided by the sender
     * @return The list of tab completions for this command
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 2) {
            return IridiumFactions.getInstance().getBankItemList().stream()
                    .map(BankItem::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
