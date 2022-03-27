package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.FactionRelationshipsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class RelationshipsCommand extends Command {

    /**
     * The default constructor.
     */
    public RelationshipsCommand() {
        super(Collections.singletonList("relationships"), "View your faction relationships", "%prefix% &7/f relationships <ally/enemy>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2 || (!args[1].equalsIgnoreCase("ally") && !args[1].equalsIgnoreCase("enemy"))) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        RelationshipType relationshipType = args[1].equalsIgnoreCase("ally") ? RelationshipType.ALLY : RelationshipType.ENEMY;
        player.openInventory(new FactionRelationshipsGUI(faction, relationshipType).getInventory());
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
        // We currently don't want to tab-completion here
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

}
