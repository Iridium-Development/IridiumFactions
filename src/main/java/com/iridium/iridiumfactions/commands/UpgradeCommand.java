package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.Upgrade;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionUpgrade;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.FactionUpgradeGUI;
import com.iridium.iridiumfactions.upgrades.UpgradeData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class UpgradeCommand extends Command {

    /**
     * The default constructor.
     */
    public UpgradeCommand() {
        super(Arrays.asList("upgrades", "upgrade"), "View your faction upgrades", "%prefix% &7/f upgrade <name>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.openInventory(new FactionUpgradeGUI(user.getFaction()).getInventory());
            return true;
        }
        String upgradeName = args[1];
        Upgrade<?> upgrade = IridiumFactions.getInstance().getUpgradesList().get(upgradeName);
        if (upgrade == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownUpgrade.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        FactionUpgrade factionUpgrade = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, upgradeName);
        UpgradeData upgradeData = upgrade.upgrades.get(factionUpgrade.getLevel() + 1);
        if (upgradeData == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().maxLevelReached.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getEconomy().getBalance(player) < upgradeData.money) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotAfford.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        factionUpgrade.setLevel(factionUpgrade.getLevel() + 1);
        IridiumFactions.getInstance().getEconomy().withdrawPlayer(player, upgradeData.money);
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().successfullyBoughtUpgrade
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%upgrade%", upgrade.name)
                .replace("%cost%", IridiumFactions.getInstance().getNumberFormatter().format(upgradeData.money))
        ));
        return true;
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
        return new ArrayList<>(IridiumFactions.getInstance().getUpgradesList().keySet());
    }

}
