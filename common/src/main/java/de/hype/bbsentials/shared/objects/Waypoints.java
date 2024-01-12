package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;

import java.util.HashMap;
import java.util.Map;

public class Waypoints {
    public static int counter = 0;
    public static Map<Integer, Waypoints> waypoints = new HashMap<>();
    public Position position;
    public int renderDistance;
    public String jsonToRenderText;
    public boolean deleteOnServerSwap;
    public boolean visible;
    public String textureNameSpace;
    public String texturePath;
    int removeRunnableId;
    int waypointId;

    public Waypoints(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        waypointId = counter++;
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.texturePath = texturePath;
        this.textureNameSpace = textureNameSpace;
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
