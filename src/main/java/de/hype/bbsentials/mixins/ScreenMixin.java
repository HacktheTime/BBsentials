package de.hype.bbsentials.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "getTooltipFromItem", at = @At("RETURN"), cancellable = true)
    private static void getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> ci) {
        /*// Cancel the original method
        List<Text> temp = ci.getReturnValue();
        temp.add(1,Text.literal("§6Splash on going by missing"));
        ci.setReturnValue(temp);*/

        //TODO this is an only visual addition for time tooltip.
        // This means it can be used to add data the server cant see.
    }
}
