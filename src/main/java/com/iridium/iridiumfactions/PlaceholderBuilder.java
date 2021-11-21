package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumfactions.database.Faction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlaceholderBuilder {
    private final List<Placeholder> placeholderList = new ArrayList<>();

    public PlaceholderBuilder() {
        placeholderList.add(new Placeholder("prefix", IridiumFactions.getInstance().getConfiguration().prefix));
    }

    public static CompletableFuture<List<Placeholder>> getFactionPlaceholders(Faction faction) {
        return CompletableFuture.supplyAsync(() -> {
            List<Placeholder> placeholderList = new ArrayList<>();

            placeholderList.add(new Placeholder("faction_name", faction.getName()));
            placeholderList.add(new Placeholder("faction_owner", faction.getOwner().getName()));
            placeholderList.add(new Placeholder("faction_value", String.valueOf(faction.getValue().join())));
            placeholderList.add(new Placeholder("faction_rank", String.valueOf(faction.getRank().join())));
            placeholderList.add(new Placeholder("faction_create", faction.getCreateTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat))));

            IridiumFactions.getInstance().getBlockValues().blockValues.keySet().stream()
                    .map(material -> new Placeholder(material.name() + "_AMOUNT", String.valueOf(faction.getBlockCountCache().getOrDefault(material, 0))))
                    .forEach(placeholderList::add);
            return placeholderList;
        });
    }
}
