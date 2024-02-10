package com.iridium.iridiumfactions.placeholders;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.PlaceholderBuilder;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.bank.BankItem;
import org.bukkit.entity.EntityType;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionPlaceholderBuilder implements PlaceholderBuilder<Faction> {
    @Override
    public List<Placeholder> getPlaceholders(Faction faction) {
        List<User> users = IridiumFactions.getInstance().getTeamManager().getTeamMembers(faction);
        List<String> onlineUsers = users.stream()
                .filter(u -> u.getPlayer() != null)
                .map(User::getName)
                .collect(Collectors.toList());
        List<String> offlineUsers = users.stream()
                .filter(u -> u.getPlayer() == null)
                .map(User::getName)
                .collect(Collectors.toList());

        List<Placeholder> placeholderList = new ArrayList<>(Arrays.asList(
                new Placeholder("faction_name", faction.getName()),
                new Placeholder("faction_owner", IridiumFactions.getInstance().getTeamManager().getTeamMembers(faction).stream()
                        .filter(user -> user.getUserRank() == Rank.OWNER.getId())
                        .findFirst()
                        .map(User::getName)
                        .orElse("N/A")),
                new Placeholder("faction_create", faction.getCreateTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat))),
                new Placeholder("faction_description", faction.getDescription()),
                new Placeholder("faction_value", String.valueOf(IridiumFactions.getInstance().getTeamManager().getTeamValue(faction))),
                new Placeholder("faction_level", String.valueOf(faction.getLevel())),
                new Placeholder("faction_experience", String.valueOf(faction.getExperience())),
                new Placeholder("faction_value_rank", String.valueOf(IridiumFactions.getInstance().getTop().valueTeamSort.getRank(faction, IridiumFactions.getInstance()))),
                new Placeholder("faction_experience_rank", String.valueOf(IridiumFactions.getInstance().getTop().experienceTeamSort.getRank(faction, IridiumFactions.getInstance()))),
                new Placeholder("faction_members_online", String.join(", ", onlineUsers)),
                new Placeholder("faction_members_online_count", String.valueOf(onlineUsers.size())),
                new Placeholder("faction_members_offline", String.join(", ", offlineUsers)),
                new Placeholder("faction_members_offline_count", String.valueOf(offlineUsers.size())),
                new Placeholder("faction_members_count", String.valueOf(users.size())),
                new Placeholder("faction_total_power", String.valueOf(faction.getTotalPower())),
                new Placeholder("faction_remaining_power", String.valueOf(faction.getRemainingPower())),
                new Placeholder("faction_land", String.valueOf(IridiumFactions.getInstance().getFactionManager().getFactionClaims(faction).size()))
        ));
        for (BankItem bankItem : IridiumFactions.getInstance().getBankItemList()) {
            placeholderList.add(new Placeholder("faction_bank_" + bankItem.getName().toLowerCase(), String.valueOf(IridiumFactions.getInstance().getTeamManager().getTeamBank(faction, bankItem.getName()).getNumber())));
        }
        for (XMaterial xMaterial : XMaterial.values()) {
            placeholderList.add(new Placeholder("faction_" + xMaterial.name().toLowerCase() + "_amount", String.valueOf(IridiumFactions.getInstance().getTeamManager().getTeamBlock(faction, xMaterial).getAmount())));
        }
        for (EntityType entityType : EntityType.values()) {
            placeholderList.add(new Placeholder("faction_" + entityType.name().toLowerCase() + "_amount", String.valueOf(IridiumFactions.getInstance().getTeamManager().getTeamSpawners(faction, entityType).getAmount())));
        }
        return placeholderList;
    }

    public List<Placeholder> getDefaultPlaceholders() {
        List<Placeholder> placeholderList = new ArrayList<>(Arrays.asList(
                new Placeholder("faction_name", "N/A"),
                new Placeholder("faction_owner", "N/A"),
                new Placeholder("faction_description", "N/A"),
                new Placeholder("faction_create", "N/A"),
                new Placeholder("faction_value", "N/A"),
                new Placeholder("faction_level", "N/A"),
                new Placeholder("faction_experience", "N/A"),
                new Placeholder("faction_value_rank", "N/A"),
                new Placeholder("faction_experience_rank", "N/A"),
                new Placeholder("faction_members_online", "N/A"),
                new Placeholder("faction_members_online_count", "N/A"),
                new Placeholder("faction_members_offline", "N/A"),
                new Placeholder("faction_members_offline_count", "N/A"),
                new Placeholder("faction_members_count", "N/A"),
                new Placeholder("faction_total_power", "N/A"),
                new Placeholder("faction_remaining_power", "N/A"),
                new Placeholder("faction_land", "N/A")
        ));
        for (BankItem bankItem : IridiumFactions.getInstance().getBankItemList()) {
            placeholderList.add(new Placeholder("faction_bank_" + bankItem.getName().toLowerCase(), "N/A"));
        }
        for (XMaterial xMaterial : XMaterial.values()) {
            placeholderList.add(new Placeholder("faction_" + xMaterial.name().toLowerCase() + "_amount", "N/A"));
        }
        for (EntityType entityType : EntityType.values()) {
            placeholderList.add(new Placeholder("faction_" + entityType.name().toLowerCase() + "_amount", "N/A"));
        }
        return placeholderList;
    }

    @Override
    public List<Placeholder> getPlaceholders(Optional<Faction> optional) {
        return optional.isPresent() ? getPlaceholders(optional.get()) : getDefaultPlaceholders();
    }
}
