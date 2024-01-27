package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.objects.WaypointRoute;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoutesConfigScreen extends SelectionScreen<WaypointRoute> {
    public RoutesConfigScreen(Screen parent) {
        super(parent, "Routes");
    }

    @Override
    public List<WaypointRoute> getObjectList() {
        List<WaypointRoute> routes = new ArrayList<>();
        try {
            for (File file : WaypointRoute.waypointRouteDirectory.listFiles()) {
                try {

                } catch (Exception ignored) {
                    if (file.getName().endsWith(".json")) {
                        routes.add(WaypointRoute.loadFromFile(file));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return routes;
    }

    @Override
    public WaypointRoute getNewDefaultObject() {
        return new WaypointRoute("", new ArrayList<>());
    }

    /**
     * What do you want to happen when the button is clicked?
     *
     * @param object       the object the button is initialised with.
     * @param buttonWidget
     */
    @Override
    public void doOnButtonClick(WaypointRoute object, ButtonWidget buttonWidget) {
        setScreen(new RouteConfigScreen(this, object));
    }

    @Override
    public String getButtonString(WaypointRoute object) {
        return object.name;
    }

    public void done() {
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

