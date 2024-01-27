package de.hype.bbsentials.shared.objects;


import de.hype.bbsentials.client.common.client.BBsentials;

import java.awt.*;

public class ClientWaypointData extends WaypointData {
    public static int counter = 0;
    protected int waypointId = counter++;

    public ClientWaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath, Color color, boolean doTracer) {
        super(pos, jsonTextToRender, renderDistance, visible, deleteOnServerSwap, textureNameSpace, texturePath, color, doTracer);
    }

    public ClientWaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        super(pos, jsonTextToRender, renderDistance, visible, deleteOnServerSwap, textureNameSpace, texturePath, BBsentials.visualConfig.waypointDefaultColor, BBsentials.visualConfig.waypointDefaultWithTracer);
    }


    public int getWaypointId() {
        return waypointId;
    }


}
