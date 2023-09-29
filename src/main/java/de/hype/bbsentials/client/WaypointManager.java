package de.hype.bbsentials.client;

import java.util.ArrayList;
import java.util.List;

public class WaypointManager {
    private List<Waypoint> waypoints = new ArrayList<>();
    public WaypointRenderer renderer;

    WaypointManager(){
        renderer=new WaypointRenderer(this);
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public void removeWaypoint(Waypoint waypoint) {
        waypoints.remove(waypoint);
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }
}
