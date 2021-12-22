package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionChatType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatCommand extends Command {

    /**
     * The default constructor.
     */
    public ChatCommand() {
        super(Arrays.asList("chat", "c"), "Change your faction chat", "%prefix% &7/f chat <Ally/Enemy/Faction/None>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        FactionChatType factionChatType = FactionChatType.fromString(args[1]);
        if (factionChatType == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownFactionChatType
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%type%", args[1]))
            );
            return false;
        }
        user.setFactionChatType(factionChatType);
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().setFactionChatType
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%type%", factionChatType.name()))
        );
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
        return Arrays.stream(FactionChatType.values())
                .flatMap(factionChatType -> factionChatType.getAliases().stream())
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

}
