package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.shared.objects.WaypointData;

/**
 * Used to tell the addon what message came in.
 */
public class WaypointAddonPacket extends AbstractAddonPacket {
    public final WaypointData waypoint;
    public final int waypointId;
    public final Operation operation;

    public WaypointAddonPacket(WaypointData waypoint, int waypointId, Operation operation) {
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
