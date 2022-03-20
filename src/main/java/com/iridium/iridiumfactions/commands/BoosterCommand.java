package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.Booster;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBooster;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.FactionBoostersGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class BoosterCommand extends Command {

    /**
     * The default constructor.
     */
    public BoosterCommand() {
        super(Arrays.asList("booster", "boosters"), "View your faction boosters", "%prefix% &7/f booster <name>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.openInventory(new FactionBoostersGUI(user.getFaction()).getInventory());
            return true;
        }
        String boosterName = args[1];
        Booster booster = IridiumFactions.getInstance().getBoosterList().get(boosterName);
        if (booster == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownBooster.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        FactionBooster factionBooster = IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, boosterName);
        if (factionBooster.isActive() &&!booster.stackable) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().boosterAlreadyActive.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getEconomy().getBalance(player) < booster.cost) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotAfford.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        IridiumFactions.getInstance().getEconomy().withdrawPlayer(player, booster.cost);
        factionBooster.setTime(LocalDateTime.now().plusSeconds(booster.time + (factionBooster.isActive() && booster.stackable ? factionBooster.getRemainingTime() : 0)));
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().successfullyBoughtBooster
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%booster%", booster.name)
                .replace("%cost%", IridiumFactions.getInstance().getNumberFormatter().format(booster.cost))
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
        return new ArrayList<>(IridiumFactions.getInstance().getBoosterList().keySet());
    }

}
