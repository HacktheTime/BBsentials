package de.hype.bbsentials.client.common.objects;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.objects.WaypointData;

import java.util.HashMap;
import java.util.Map;

public class Waypoints extends WaypointData {
    public static int counter = 0;
    public static Map<Integer, Waypoints> waypoints = new HashMap<>();
    int removeRunnableId;
    int waypointId;

    public Waypoints(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        super(pos, jsonTextToRender, renderDistance, visible, deleteOnServerSwap, textureNameSpace, texturePath);
        ServerSwitchTask.onServerLeaveTask(() -> {
            if (this.deleteOnServerSwap)
                this.removeFromPool();
        });
        waypoints.put(waypointId, this);
    }

    public Waypoints removeFromPool() {
       BBsentials.onServerLeave.remove(removeRunnableId);
        return waypoints.remove(waypointId);
    }

    public int getWaypointId() {
        return waypointId;
    }
}
