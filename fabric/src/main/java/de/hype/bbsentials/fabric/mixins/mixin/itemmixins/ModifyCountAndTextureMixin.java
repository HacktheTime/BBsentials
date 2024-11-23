package de.hype.bbsentials.fabric.mixins.mixin.itemmixins;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(DrawContext.class)
public abstract class ModifyCountAndTextureMixin {
    @Shadow
    public abstract void drawGuiTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height);

    @ModifyVariable(
            method = "drawStackCount",
            at = @At(value = "LOAD", ordinal = 0),
            argsOnly = true
    )
    private String BBsentials$modifyStackCountText(String stackCountText, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack == null) return stackCountText;
        String data = (((ICusomItemDataAccess) (Object) stack)).getItemCountCustom();
        if (data != null) {
            return data;
        }
        else return stackCountText;
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void BBsentials$drawCustomTexture(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        var customTexture = data.BBsentialsAll$getCustomItemTexture();
        if (customTexture != null) {
            drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(customTexture), x, y, 16, 16);
            ci.cancel();
        }
    }
}
