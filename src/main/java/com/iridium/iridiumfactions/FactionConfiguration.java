package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class FactionConfiguration {
    public String defaultName;
    @NotNull
    public String defaultDescription;
}
