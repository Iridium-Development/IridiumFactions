package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum FactionChatType {
    FACTION(Arrays.asList("f", "faction"), List.of(RelationshipType.OWN)),
    ALLY(Arrays.asList("a", "ally"), Arrays.asList(RelationshipType.ALLY, RelationshipType.OWN)),
    ENEMY(Arrays.asList("e", "enemy"), Arrays.asList(RelationshipType.ENEMY, RelationshipType.OWN)),
    NONE(Arrays.asList("n", "none"), Collections.emptyList());

    private final List<String> aliases;
    private final List<RelationshipType> relationshipType;

    @Nullable
    public static FactionChatType fromString(String s) {
        for (FactionChatType type : values()) {
            if (type.aliases.contains(s.toLowerCase())) {
                return type;
            }
        }
        return null;
    }

}
