package com.iridium.iridiumfactions.utils;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Various utils which perform operations on {@link Location}'s.
 */
public class LocationUtils {

    /**
     * With the data pack, you can modify the height limits and in the Spigot API.
     * It exists since 1.17 on Spigot and 1.16 at PaperMC.
     *
     * @param world The world
     * @return The lowest AIR location.
     */
    public static int getMinHeight(World world) {
        return XMaterial.getVersion() >= 17 ? world.getMinHeight() : 0;  // World#getMinHeight() -> Available only in 1.17 Spigot and 1.16.5 PaperMC
    }

}
