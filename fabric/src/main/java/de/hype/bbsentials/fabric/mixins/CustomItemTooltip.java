package de.hype.bbsentials.fabric.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.fabric.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class CustomItemTooltip<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    @Final
    protected T handler;

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    protected CustomItemTooltip(Text title) {
        super(title);
    }

    @Shadow
    protected abstract List<Text> getTooltipFromItem(ItemStack stack);

    @ModifyExpressionValue(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack modfiedItemStack(ItemStack original) {
        ItemStack itemStack = original.copy();
        if (itemStack.  getItem() == Items.EMERALD_BLOCK || itemStack.getItem() == Items.IRON_BLOCK || itemStack.getItem() == Items.PAPER) {
            Utils.doBingoRankManipulations(itemStack);
        }
        if (BBsentials.developerConfig.hypixelItemInfo) {
            Utils.addDebugInfoToRender(itemStack);
        }
        return itemStack;
    }
}
