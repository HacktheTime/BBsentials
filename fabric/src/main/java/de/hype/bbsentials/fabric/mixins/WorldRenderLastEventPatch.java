package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.fabric.RenderInWorldContext;
import de.hype.bbsentials.fabric.Utils;
import de.hype.bbsentials.fabric.objects.WorldRenderLastEvent;
import de.hype.bbsentials.shared.objects.Waypoints;
import kotlin.Unit;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
// Credits go to nea89 for this (Firmanent)!

@Mixin(WorldRenderer.class)
public class WorldRenderLastEventPatch {
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/Camera;)V", shift = At.Shift.BEFORE))
    public void onWorldRenderLast(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        WorldRenderLastEvent event = new WorldRenderLastEvent(
                matrices, tickDelta, renderBlockOutline,
                camera, gameRenderer, lightmapTextureManager, projectionMatrix,
                this.bufferBuilders.getEntityVertexConsumers()
        );
        Utils.renderWaypoints(event);
    }

}


