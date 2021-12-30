package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command which reloads all configuration files.
 */
public class EditWarpCommand extends Command {

    /**
     * The default constructor.
     */
    public EditWarpCommand() {
        super(Collections.singletonList("editwarp"), "Edit a Faction warp", "%prefix% &7/f editwarp <name> <icon/description>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length < 3) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String name = args[1];
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.MANAGE_WARPS)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotEditWarp
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        Optional<FactionWarp> factionWarp = IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction, name);

        if (!factionWarp.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        switch (args[2]) {
            case "icon":
                if (args.length != 4) {
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix+" &7/f editwarp " + name + " icon <icon>"));
                    return false;
                }

                Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(args[3]);
                if (xMaterial.isPresent()) {
                    factionWarp.get().setIcon(xMaterial.get());
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().warpIconSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                    return true;
                } else {
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noSuchMaterial.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix).replace("%material%", args[3])));
                    return false;
                }
            case "description":
                if (args.length < 4) {
                    factionWarp.get().setDescription(null);
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().warpDescriptionRemoved.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                } else {
                    String description = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                    factionWarp.get().setDescription(description);
                    player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().warpDescriptionSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix).replace("%description%", factionWarp.get().getDescription())));
                }
                return true;
            default:
                player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
        Faction faction = IridiumFactions.getInstance().getUserManager().getUser((OfflinePlayer) commandSender).getFaction();
        List<FactionWarp> factionWarps = IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction);

        if (args.length == 2) {
            return factionWarps.stream()
                    .map(FactionWarp::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            return Arrays.asList("icon", "description");
        }

        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("icon")) {
                return Arrays.stream(XMaterial.values())
                        .map(XMaterial::name)
                        .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

}
