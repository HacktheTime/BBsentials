package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Temphook {
    public static void renderItemOverlayPost(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
        if (BBsentials.config.highlightitem) {
            if (stack != null) {
                if (stack.getDisplayName().contains(BBsentials.connection.getItemName())) {
                    stack.setStackDisplayName("§6(Bingo Splash) " + stack.getDisplayName());
                }
            }
        }
    }
}