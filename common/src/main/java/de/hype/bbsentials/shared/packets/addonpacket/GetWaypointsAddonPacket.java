package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.shared.objects.ClientWaypointData;
import de.hype.bbsentials.shared.objects.WaypointData;

import java.util.List;
import java.util.Map;

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
