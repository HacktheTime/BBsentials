package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
    @Inject(method = "getTooltipFromItem", at = @At("RETURN"), cancellable = true)
    private static void getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> ci) {
        List<Text> text = (((ICusomItemDataAccess) (Object) stack)).BBsentialsAll$getItemRenderTooltip();
    }
}
