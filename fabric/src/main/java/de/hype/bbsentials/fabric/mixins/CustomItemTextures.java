package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class CustomItemTextures {
    @Shadow
    public abstract void drawGuiTexture(Identifier texture, int x, int y, int width, int height);

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        for (CustomItemTexture itemTexture : BBsentials.customItemTextures.values()) {
            if (itemTexture.isItem(stack.getName().getString(),"",stack.getNbt().toString(),stack))
                drawGuiTexture(new Identifier(itemTexture.nameSpace, itemTexture.renderTextureId), x, y, 16, 16);
            ci.cancel();
        }
    }
}
