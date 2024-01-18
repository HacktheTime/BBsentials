package de.hype.bbsentials.shared.objects;

public class WaypointData {
    public Position position;
    public int renderDistance;
    public String jsonToRenderText;
    public boolean deleteOnServerSwap;
    public boolean visible;
    public String textureNameSpace;
    public String texturePath;

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, String textureNameSpace, String texturePath) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.texturePath = texturePath;
        this.textureNameSpace = textureNameSpace;
    }
}
