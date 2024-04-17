package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.Utils;
import de.hype.bbsentials.fabric.objects.WorldRenderLastEvent;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// Credits go to nea89 for this (Firmanent)!

@Mixin(WorldRenderer.class)
public abstract class WorldRenderLastEventPatch implements SynchronousResourceReloader, AutoCloseable {
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/Camera;)V", shift = At.Shift.BEFORE))
    public void BBsentials$onWorldRenderLast(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        WorldRenderLastEvent event = new WorldRenderLastEvent(
                matrices, tickDelta, renderBlockOutline,
                camera, gameRenderer, lightmapTextureManager, projectionMatrix,
                this.bufferBuilders.getEntityVertexConsumers()
        );
        Utils.renderWaypoints(event);
    }

}


