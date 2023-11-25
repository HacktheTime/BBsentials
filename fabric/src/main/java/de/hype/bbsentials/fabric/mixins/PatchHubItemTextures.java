package de.hype.bbsentials.fabric.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class PatchHubItemTextures {
    @Shadow
    public abstract void drawGuiTexture(Identifier texture, int x, int y, int width, int height);

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        if (stack.getName().getString().contains("Hub")) {
            // TODO: maybe remove .png
            drawGuiTexture(new Identifier("bbsentials:textures/item/splash_hub.png"), x, y, 16, 16);
            ci.cancel();
        }
    }
}
