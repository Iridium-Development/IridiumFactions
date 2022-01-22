
package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionAccess;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.User;
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
public class AccessCommand extends Command {

    /**
     * The default constructor.
     */
    public AccessCommand() {
        super(Collections.singletonList("access"), "View your faction chest", "%prefix% &7/f access <FactionRank> <ALLOW/DENY>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        Optional<FactionClaim> factionClaim = IridiumFactions.getInstance().getFactionManager().getFactionClaimViaChunk(player.getLocation().getChunk());
        if (!factionClaim.isPresent() || factionClaim.get().getFactionID() != faction.getId()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (args.length == 1) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAccessListHeader.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            for (FactionRank factionRank : FactionRank.values()) {
                Optional<FactionAccess> factionAccess = IridiumFactions.getInstance().getDatabaseManager().getFactionAccessTableManager().getEntry(new FactionAccess(faction, factionClaim.get(), user.getFactionRank(), true));
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionRankAccess
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%rank%", factionRank.getDisplayName())
                        .replace("%access%", factionAccess.map(FactionAccess::isAllowed).orElse(true) ? "ALLOWED" : "DENIED")
                ));
            }
            return true;
        } else if (args.length == 3) {
            Optional<FactionRank> factionRank = FactionRank.getByName(args[1]);
            if (!factionRank.isPresent()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownFactionRank.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
            boolean allow;
            switch (args[2].toLowerCase()) {
                case "allow":
                    allow = true;
                    break;
                case "deny":
                    allow = false;
                    break;
                default:
                    player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                    return false;
            }
            IridiumFactions.getInstance().getFactionManager().setFactionAccess(faction, factionRank.get(), factionClaim.get(), allow);
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAccessSet
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%access%", allow ? "allowed" : "denied")
                    .replace("%rank%", factionRank.get().getDisplayName())
            ));
            return true;
        } else {
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
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String
            label, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(FactionRank.values()).map(FactionRank::getDisplayName).collect(Collectors.toList());
        }
        if (args.length == 3) {
            return Arrays.asList("allow", "deny");
        }
        // We currently don't want to tab-completion here
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

}
