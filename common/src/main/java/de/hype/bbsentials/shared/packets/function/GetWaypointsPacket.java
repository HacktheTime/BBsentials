package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.ClientWaypointData;

import java.util.List;

/**
 * Used to tell the addon what message came in.
 */
public class GetWaypointsPacket extends AbstractPacket {
    public final List<ClientWaypointData> waypoints;

    public GetWaypointsPacket(List<ClientWaypointData> waypoints) {
        super(1, 1); //Min and Max supported Version
        this.waypoints = waypoints;
    }
}
