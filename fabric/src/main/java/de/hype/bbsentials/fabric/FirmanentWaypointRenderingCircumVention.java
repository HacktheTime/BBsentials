package de.hype.bbsentials.fabric;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.NotNull;

public class FirmanentWaypointRenderingCircumVention extends RenderLayer {
    public FirmanentWaypointRenderingCircumVention(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @NotNull
    public static RenderLayer tryTest() {
        return RenderLayer.of("bbsentials_firmament_translucent_tris",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                RenderLayer.DEFAULT_BUFFER_SIZE,
                false, true, getFirmanentRender()
        );
    }

    private static MultiPhaseParameters getFirmanentRender() {
        return RenderLayer.MultiPhaseParameters.builder()
                .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .program(RenderPhase.COLOR_PROGRAM)
                .build(false);
    }
}
