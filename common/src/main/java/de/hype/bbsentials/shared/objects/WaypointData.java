package de.hype.bbsentials.shared.objects;

import java.awt.*;

public class WaypointData {
    public Position position;
    public int renderDistance;
    public String jsonToRenderText;
    public boolean deleteOnServerSwap;
    public boolean visible;
    public String textureNameSpace;
    public String texturePath;
    public Color color;

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.texturePath = texturePath;
        this.textureNameSpace = textureNameSpace;
        this.color = new Color(1f, 1f, 1f);
        if (jsonTextToRender == null || jsonTextToRender.isEmpty()) {
            this.jsonToRenderText = "{\"text\":\"Unnamed\"}";
        }
    }

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath, Color color) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.texturePath = texturePath;
        this.textureNameSpace = textureNameSpace;
        this.color = color;
    }
}
