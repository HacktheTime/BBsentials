package de.hype.bingonet.fabric.mixins.mixin.itemmixins;

import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
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
    private String BingoNet$modifyStackCountText(String stackCountText, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack == null) return stackCountText;
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        if (data != null) {
            String text = data.getItemCountCustom();
            if (text != null) return text;
            else return stackCountText;
        }
        else return stackCountText;
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void BingoNet$drawCustomTexture(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        if (stack == null) return;
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        if (data == null) return;
        String customTexture = data.BingoNetAll$getCustomItemTexture();
        if (customTexture != null) {
            drawGuiTexture(RenderLayer::getGuiTextured, Identifier.of(customTexture), x, y, 16, 16);
            ci.cancel();
        }
    }
}
