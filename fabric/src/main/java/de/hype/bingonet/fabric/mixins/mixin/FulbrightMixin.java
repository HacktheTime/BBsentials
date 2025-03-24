package de.hype.bingonet.fabric.mixins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hype.bingonet.client.common.client.BingoNet;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class FulbrightMixin {
    @ModifyExpressionValue(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Double;floatValue()F",
                    ordinal = 1
            )
    )
    private float modifyLightValue(float original) {
        if (BingoNet.visualConfig.doGammaOverride) return 1000.0F;
        return original;
    }
}
