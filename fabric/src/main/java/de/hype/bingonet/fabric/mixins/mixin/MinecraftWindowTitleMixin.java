package de.hype.bingonet.fabric.mixins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.hype.bingonet.client.common.client.BingoNet;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftWindowTitleMixin {

    @ModifyReturnValue(method = "getWindowTitle", at = @At("RETURN"))
    private String getWindowTitle(String original) {
        String append = BingoNet.visualConfig.appendMinecraftWindowTitle;
        if (append != null && !append.isEmpty()) {
            return append.replace("%username%", BingoNet.generalConfig.getUsername()).replace("%default%", original);
        }
        return original;
    }
}
