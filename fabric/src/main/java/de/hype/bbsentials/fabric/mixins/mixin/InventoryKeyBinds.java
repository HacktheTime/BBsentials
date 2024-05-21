package de.hype.bbsentials.fabric.mixins.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GenericContainerScreen.class)
public abstract class InventoryKeyBinds extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {


    public InventoryKeyBinds(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Shadow
    @Override
    protected abstract void drawBackground(DrawContext context, float delta, int mouseX, int mouseY);

    @Intrinsic(displace = true)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedSlot != null && keyCode == 258 && focusedSlot.getStack().getItem() != Items.AIR)
            onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.QUICK_MOVE);
        else return super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Intrinsic(displace = true)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}