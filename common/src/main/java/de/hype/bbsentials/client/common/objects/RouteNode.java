package de.hype.bbsentials.client.common.objects;

import de.hype.bbsentials.shared.objects.Position;

import java.awt.*;

public class RouteNode {
    public static final int DEFAULT_TRIGGER_NEXT_RANGE = 3;
    public Position coords;
    public String name;
    public Color color;
    int triggerNextRange = -1;
    transient WaypointRoute route;

    public RouteNode(Position coords, Color color, int triggerNextRange, String name, WaypointRoute route) {
        this.coords = coords;
        this.color = color;
        this.triggerNextRange = triggerNextRange;
        this.name = name;
//        this.nodeId = route.getNextNodeId();
        this.route = route;
    }

    public RouteNode(int x, int y, int z, float r, float g, float b, String name, WaypointRoute route) {
        coords = new Position(x, y, z);
        this.color = new Color(r, g, b);
//        this.nodeId = Integer.parseInt(name);
        this.route = route;
    }


}
