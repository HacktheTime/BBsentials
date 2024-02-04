package de.hype.bbsentials.shared.objects;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class WaypointData {
    public Position position;
    public int renderDistance;
    public String jsonToRenderText;
    public boolean deleteOnServerSwap;
    public boolean visible;
    public List<RenderInformation> render;
    public Color color;
    public boolean doTracer = true;

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, RenderInformation render, boolean doTracer) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.render = Arrays.asList(render);
        this.visible = visible;
        this.doTracer = doTracer;
        this.color = new Color(1f, 1f, 1f);
        if (jsonTextToRender == null || jsonTextToRender.isEmpty()) {
            this.jsonToRenderText = "{\"text\":\"Unnamed\"}";
        }
    }

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, RenderInformation render, Color color, boolean doTracer) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.render = Arrays.asList(render);
        this.color = color;
        this.doTracer = doTracer;
        if (jsonTextToRender == null || jsonTextToRender.isEmpty()) {
            this.jsonToRenderText = "{\"text\":\"Unnamed\"}";
        }
    }

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, List<RenderInformation> render, boolean doTracer) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.render = render;
        this.visible = visible;
        this.doTracer = doTracer;
        this.color = new Color(1f, 1f, 1f);
        if (jsonTextToRender == null || jsonTextToRender.isEmpty()) {
            this.jsonToRenderText = "{\"text\":\"Unnamed\"}";
        }
    }

    public WaypointData(Position pos, String jsonTextToRender, int renderDistance, boolean visible, boolean deleteOnServerSwap, List<RenderInformation> render, Color color, boolean doTracer) {
        this.position = pos;
        this.jsonToRenderText = jsonTextToRender;
        this.renderDistance = renderDistance;
        this.deleteOnServerSwap = deleteOnServerSwap;
        this.visible = visible;
        this.render = render;
        this.color = color;
        this.doTracer = doTracer;
        if (jsonTextToRender == null || jsonTextToRender.isEmpty()) {
            this.jsonToRenderText = "{\"text\":\"Unnamed\"}";
        }
    }
}
