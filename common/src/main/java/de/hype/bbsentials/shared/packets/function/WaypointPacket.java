package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.WaypointData;

/**
 * Used to tell the addon what message came in.
 */
public class WaypointPacket extends AbstractPacket {
    public final WaypointData waypoint;
    public final int waypointId;
    public final Operation operation;

    public WaypointPacket(WaypointData waypoint, int waypointId, Operation operation) {
        super(1, 1); //Min and Max supportet Version
        this.waypoint = waypoint;
        this.waypointId = waypointId;
        this.operation = operation;
    }

    public enum Operation {
        ADD,
        REMOVE,
        EDIT
    }
}
