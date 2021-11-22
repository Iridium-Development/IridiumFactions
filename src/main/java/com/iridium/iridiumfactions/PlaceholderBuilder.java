package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumfactions.database.Faction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceholderBuilder {

    public static List<Placeholder> getFactionPlaceholders(Faction faction) {
        List<Placeholder> placeholderList = new ArrayList<>();

        placeholderList.add(new Placeholder("faction_name", faction.getName()));
        placeholderList.add(new Placeholder("faction_owner", faction.getOwner().getName()));
        placeholderList.add(new Placeholder("faction_value", String.valueOf(faction.getValue())));
        placeholderList.add(new Placeholder("faction_rank", String.valueOf(faction.getRank())));
        placeholderList.add(new Placeholder("faction_create", faction.getCreateTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat))));

        IridiumFactions.getInstance().getBlockValues().blockValues.keySet().stream()
                .map(material -> new Placeholder(material.name() + "_AMOUNT", String.valueOf(IridiumFactions.getInstance().getFactionManager().getFactionBlockAmount(faction, material))))
                .forEach(placeholderList::add);
        return placeholderList;
    }
}
