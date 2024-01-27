package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.objects.RenderInformation;
import de.hype.bbsentials.shared.objects.WaypointData;

import java.awt.*;
import java.util.Collection;

public class WaypointPacket extends AbstractPacket {
    public Collection<WaypointPacketData> waypoint;

    public WaypointPacket(Collection<WaypointPacketData> waypoint) {
        super(1, 1);
        this.waypoint = waypoint;
    }

    public enum WaypointAction {
        ADD,
        REMOVE,
        UPDATE
    }

    public class WaypointPacketData extends WaypointData {

        public WaypointAction action;

        public WaypointPacketData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, RenderInformation render, Color color, boolean doTracer, WaypointAction action) {
            super(pos, jsonTextToRender, renderDistance, visible, deleteOnServerSwap, render, color, doTracer);
            this.action = action;
        }

        public WaypointData getAsWaypointData() {
            return this;
        }
    }
}
