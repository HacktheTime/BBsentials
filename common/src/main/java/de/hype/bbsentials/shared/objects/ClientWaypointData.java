package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.client.common.api.Formatting;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

public class ClientWaypointData extends WaypointData {
    public static int counter = 0;
    protected int waypointId = counter++;

    public ClientWaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        super(pos, jsonTextToRender, renderDistance, visible, deleteOnServerSwap, textureNameSpace, texturePath);
    }

    public String getMinimalInfoString() {
        String unformatedName;
        try {
            unformatedName = EnvironmentCore.utils.getStringFromTextJson(jsonToRenderText);
        } catch (Exception e) {
            unformatedName = Formatting.RED + "Invalid Json Name";
        }
        return "ID: " + getWaypointId() + " | Name: " + unformatedName + "§r | Coords: " + position.toString();
    }

    public int getWaypointId() {
        return waypointId;
    }

    public String getFullInfoString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ");
        try {
            builder.append(EnvironmentCore.utils.getStringFromTextJson(jsonToRenderText) + "§r\n");
        } catch (Exception e) {
            builder.append(Formatting.RED + "Invalid Json Name§r\n");
        }
        builder.append("Coords: " + position.toString() + "\n");
        builder.append("Visible: " + visible + "\n");
        builder.append("Deleted on Server Swap: " + deleteOnServerSwap + "\n");
        builder.append("Maximum Render Distance: " + renderDistance + "\n");
        String customTexture = textureNameSpace + ":" + texturePath;
        if (!customTexture.equals("null:null")) builder.append("Custom Texture: " + customTexture);
        return builder.toString();
    }
}
