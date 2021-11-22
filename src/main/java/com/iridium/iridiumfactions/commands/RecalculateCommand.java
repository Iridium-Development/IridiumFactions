package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Command which display plugin information to the user.
 */
public class RecalculateCommand extends Command {

    private BukkitTask bukkitTask;

    /**
     * The default constructor.
     */
    public RecalculateCommand() {
        super(Arrays.asList("recalculate", "recalc"), "Recalculate all Faction Values", "iridiumfactions.recalculate", false, Duration.ZERO);
    }

    /**
     * Executes the command for the specified {@link CommandSender} with the provided arguments.
     * Not called when the command execution was invalid (no permission, no player or command disabled).
     *
     * @param sender The CommandSender which executes this command
     * @param args   The arguments used with this command. They contain the sub-command
     */
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (bukkitTask != null) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().calculationAlreadyInProcess.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        int interval = 1;
        List<Faction> factionList = IridiumFactions.getInstance().getFactionManager().getFactions();
        int seconds = (factionList.size() * interval / 20) % 60;
        int minutes = (factionList.size() * interval / 20) / 60;
        sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().calculatingFactions
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds))
                .replace("%amount%", String.valueOf(factionList.size()))
        );

        bukkitTask = Bukkit.getScheduler().runTaskTimer(IridiumFactions.getInstance(), new Runnable() {
            final ListIterator<Integer> factions = factionList.stream().map(Faction::getId).collect(Collectors.toList()).listIterator();

            @Override
            public void run() {
                if (factions.hasNext()) {
                    IridiumFactions.getInstance().getFactionManager().getFactionViaId(factions.next()).ifPresent(island ->
                            IridiumFactions.getInstance().getFactionManager().recalculateFactionValue(island)
                    );
                } else {
                    bukkitTask.cancel();
                    bukkitTask = null;
                    sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().calculatingFinished.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                }
            }

        }, 0, interval);
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
        // Return a new List, so it isn't a list of online players
        return Collections.emptyList();
    }

}
