package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.fabric.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class CustomItemTooltip<T extends ScreenHandler> extends Screen {
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

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void drawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack itemStack = this.focusedSlot.getStack().copy();
            if (itemStack.getItem() == Items.EMERALD_BLOCK || itemStack.getItem() == Items.IRON_BLOCK) {
                Utils.doBingoRankManipulations(itemStack);
            }
            if (BBsentials.developerConfig.hypixelItemInfo) {
                Utils.addDebugInfoToRender(itemStack);
            }
            context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y);
        }
        ci.cancel();
    }
}
