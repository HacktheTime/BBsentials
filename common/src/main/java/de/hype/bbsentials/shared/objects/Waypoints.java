package de.hype.bbsentials.shared.objects;

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
    int waypointId;

    public Waypoints(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap) {
        waypointId = counter++;
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        waypoints.put(waypointId, this);
    }

    public Waypoints removeFromPool() {
        return waypoints.remove(waypointId);
    }
}
