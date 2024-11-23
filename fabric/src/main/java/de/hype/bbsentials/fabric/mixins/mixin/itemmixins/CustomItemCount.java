package de.hype.bbsentials.fabric.mixins.mixin.itemmixins;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class CustomItemCount {
    @ModifyVariable(
            method = "drawStackCount",
            at = @At(value = "LOAD", ordinal = 0),
            argsOnly = true
    )
    private String modifyStackCountText(String stackCountText, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (stack == null) return stackCountText;
        ICusomItemDataAccess data = (((ICusomItemDataAccess) (Object) stack));
        //noinspection UnreachableCode
        return data.BBsentialsAll$getCount();
    }
}
