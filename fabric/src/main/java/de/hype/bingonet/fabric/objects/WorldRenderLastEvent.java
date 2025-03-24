package de.hype.bingonet.fabric.objects;
// Credits go to nea89o for this (Firmanent)!
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Position;

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
    public final RenderTickCounter tickCounter;
    public final Camera camera;
    public final VertexConsumerProvider.Immediate vertexConsumers;

    public final List<TextRenderCall> toRender = new ArrayList<>();

    public WorldRenderLastEvent(MatrixStack matrices, RenderTickCounter tickCounter, Camera camera, VertexConsumerProvider.Immediate vertexConsumers) {
        this.matrices = matrices;
        this.tickCounter = tickCounter;
        this.camera = camera;
        this.vertexConsumers = vertexConsumers;
    }
}
