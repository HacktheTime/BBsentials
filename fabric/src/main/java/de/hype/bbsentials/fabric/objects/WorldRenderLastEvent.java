package de.hype.bbsentials.fabric.objects;
// Credits go to nea89 for this (Firmanent)!
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Position;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
/**
 * This event is called after all world rendering is done, but before any GUI rendering (including hand) has been done.
 */
public class WorldRenderLastEvent
//        extends FirmamentEvent
{
//    public static final FirmamentEventBus<WorldRenderLastEvent> INSTANCE = new FirmamentEventBus<>();

    public static class TextRenderCall {
        public final String string;
        public final Position position;

        public TextRenderCall(String string, Position position) {
            this.string = string;
            this.position = position;
        }
    }

    public final MatrixStack matrices;
    public final float tickDelta;
    public final boolean renderBlockOutline;
    public final Camera camera;
    public final GameRenderer gameRenderer;
    public final LightmapTextureManager lightmapTextureManager;
    public final Matrix4f positionMatrix;
    public final VertexConsumerProvider.Immediate vertexConsumers;

    public final List<TextRenderCall> toRender = new ArrayList<>();

    public WorldRenderLastEvent(MatrixStack matrices, float tickDelta, boolean renderBlockOutline, Camera camera,
                                GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                Matrix4f positionMatrix, VertexConsumerProvider.Immediate vertexConsumers) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
        this.renderBlockOutline = renderBlockOutline;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.lightmapTextureManager = lightmapTextureManager;
        this.positionMatrix = positionMatrix;
        this.vertexConsumers = vertexConsumers;
    }
}
