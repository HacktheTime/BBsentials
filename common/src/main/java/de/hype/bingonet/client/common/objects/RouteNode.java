package de.hype.bingonet.client.common.objects;

import de.hype.bingonet.shared.objects.Position;

import java.awt.*;

public class RouteNode {
    public static final int DEFAULT_TRIGGER_NEXT_RANGE = 3;
    public Position coords;
    public String name;
    public Color color;
    public boolean doTracer=true;
    public int triggerNextRange = DEFAULT_TRIGGER_NEXT_RANGE;
    transient WaypointRoute route;

    public RouteNode(Position coords, Color color, boolean doTracer, int triggerNextRange, String name, WaypointRoute route) {
        this.coords = coords;
        this.color = color;
        this.doTracer = doTracer;
        this.triggerNextRange = triggerNextRange;
        this.name = name;
//        this.nodeId = route.getNextNodeId();
        this.route = route;
    }

    public RouteNode(int x, int y, int z, float r, float g, float b, String name, boolean doTracer, WaypointRoute route) {
        this.doTracer = doTracer;
        coords = new Position(x, y, z);
        this.color = new Color(r, g, b);
        this.name=name;
//        this.nodeId = Integer.parseInt(name);
        this.route = route;
    }

    @Override
    public String toString() {
        return name + ": " + coords.toString();
    }
}
