package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnreachableCode")
@Mixin(DrawContext.class)
public abstract class CustomItemTextures {
    @Shadow
    public abstract void drawGuiTexture(Identifier texture, int x, int y, int width, int height);

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void BBsentials$onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        if (stack == null) return;
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        String tpath = data.BBsentialsAll$getCustomItemTexture();
        if (tpath != null) {
            drawGuiTexture(Identifier.of(tpath), x, y, 16, 16);
            ci.cancel();
            return;
        }
    }

    @ModifyVariable(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "HEAD"), argsOnly = true)
    public String BBsentials$renderItem(@Nullable String originalCount, TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String z) {
        if (stack == null) return originalCount;
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        //noinspection UnreachableCode
        return data.BBsentialsAll$getCount();
    }

}
