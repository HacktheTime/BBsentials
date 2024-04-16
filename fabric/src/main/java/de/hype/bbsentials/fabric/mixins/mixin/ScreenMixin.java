package de.hype.bbsentials.fabric.mixins.mixin;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
//    @ModifyReturnValue(method = "getTooltipFromItem", at = @At("RETURN"))
//    private static List<Text> getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> ci) {
//    }
}
