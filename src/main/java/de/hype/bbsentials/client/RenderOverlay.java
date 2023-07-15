package de.hype.bbsentials.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.datafixer.fix.ItemNbtFix;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class RenderOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        // Check if the item is present in the GUI
        if (BBsentials.bbserver.highlightItem()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen != null) {
                if (client.currentScreen instanceof GenericContainerScreen && client.currentScreen.getTitle().getString().equals("SkyBlock Hub Selector")) {
                    GenericContainerScreen containerScreen = (GenericContainerScreen) client.currentScreen;
                    DefaultedList<ItemStack> items = containerScreen.getScreenHandler().getStacks();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getName().getString().contains(BBsentials.bbserver.getItemName())) {
                            ItemStack found = items.get(i);
                            found.setCustomName(Text.literal("§r§6Splash Hub"));
                        }
                    }
                }
            }
        }
    }
}