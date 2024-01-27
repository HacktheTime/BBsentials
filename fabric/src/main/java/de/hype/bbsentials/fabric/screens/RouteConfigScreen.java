package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.RouteNode;
import de.hype.bbsentials.client.common.objects.WaypointRoute;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.*;
import java.util.List;

public class RouteConfigScreen extends SelectionScreen<RouteNode> {
    WaypointRoute route;

    public RouteConfigScreen(Screen parent, WaypointRoute route) {
        super(parent, "Route Nodes");
        this.route = route;
    }


    @Override
    public List<RouteNode> getObjectList() {
        return route.nodes;
    }

    @Override
    public RouteNode getNewDefaultObject() {
        return new RouteNode(EnvironmentCore.utils.getPlayersPosition(), new Color(255, 255, 255),true, -1, "Unamed", route);
    }

    /**
     * What do you want to happen when the button is clicked?
     *
     * @param object       the object the button is initialised with.
     * @param buttonWidget
     * @return what shall happen when the button is pressed
     */
    @Override
    public void doOnButtonClick(RouteNode object, ButtonWidget buttonWidget) {
        setScreen(RouteNodeConfigScreen.create(this, object));
    }

    @Override
    public String getButtonString(RouteNode object) {
        return object.name;
    }

    public void done() {
        route.save();
        close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        setScreen(parent);
        doDefaultClose();
    }
}


