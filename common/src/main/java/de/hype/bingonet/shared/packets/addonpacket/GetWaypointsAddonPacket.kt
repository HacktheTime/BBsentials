package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.shared.objects.ClientWaypointData;

import java.util.List;

/**
 * Used to tell the addon what message came in.
 */
public class GetWaypointsAddonPacket extends AbstractAddonPacket {
    public final List<ClientWaypointData> waypoints;

    public GetWaypointsAddonPacket(List<ClientWaypointData> waypoints) {
        super(1, 1); //Min and Max supported Version
        this.waypoints = waypoints;
    }
}
