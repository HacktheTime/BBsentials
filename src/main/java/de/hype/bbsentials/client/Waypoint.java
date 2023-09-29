package de.hype.bbsentials.client;

import de.hype.bbsentials.constants.enviromentShared.Islands;
import net.minecraft.util.math.BlockPos;

public class Waypoint {
    private String name;
    private BlockPos position; // Use BlockPos for precise block coordinates.
    private Islands island; // If your mod supports multiple dimensions.

    // Constructor
    public Waypoint(String name, BlockPos position, Islands dimension) {
        this.name = name;
        this.position = position;
        this.island = dimension;
    }

    // Getters
    public String getName() {
        return name;
    }

    public BlockPos getPosition() {
        return position;
    }

    public Islands getIsland() {
        return island;
    }

    public void setName(String name) {
        this.name = name;
    }

}
