package de.hype.bbsentials.fabric.mixins.mixin;

import com.google.gson.JsonParser;
import de.hype.bbsentials.fabric.ModInitialiser;
import de.hype.bbsentials.fabric.SystemUtils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(HandledScreen.class)
public abstract class InventoryKeyBinds<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

    @Nullable
    @Shadow
    protected Slot focusedSlot;

    protected InventoryKeyBinds(Text title) {
        super(title);
    }

    @Inject(method = "keyPressed",at=@At("RETURN"))
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (focusedSlot != null && focusedSlot.getStack().getItem() != Items.AIR) {
            if (keyCode == 258) onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.QUICK_MOVE);
            else if (keyCode == KeyBindingHelper.getBoundKeyOf(ModInitialiser.openWikiKeybind).getCode()) {
                NbtComponent customData = focusedSlot.getStack().get(DataComponentTypes.CUSTOM_DATA);
                if (customData == null) return;
                NbtCompound data = customData.copyNbt();
                String id = data.getString("id");
                String lowercaseID = id.toLowerCase();
                String path = "";
                if (lowercaseID.equals("enchanted_book")) {
                    path = WordUtils.capitalize((new ArrayList<>(data.getCompound("enchantments").getKeys()).get(0).replace("ultimate_", "") + "_Enchantment").replace("_", " ")).replace(" ", "_");
                }
                else if (lowercaseID.equals("pet")) {
                    path = WordUtils.capitalize((JsonParser.parseString(data.getString("petInfo")).getAsJsonObject().get("type").getAsString().toLowerCase() + "_Pet").replace("_", " ")).replace(" ", "_");
                }
                else if (lowercaseID.equals("potion")) {
                    path = WordUtils.capitalize((data.getString("potion").toLowerCase().replace("xp","XP") + "_Potion").replace("_", " ")).replace(" ", "_");
                }
                else if (lowercaseID.equals("rune")) {
                    path = WordUtils.capitalize((new ArrayList<>(data.getCompound("runes").getKeys()).get(0).toLowerCase() + "_Rune").replace("_", " ")).replace(" ", "_");
                }
                else {
                    path = id;
                }
                SystemUtils.openInBrowser("https://wiki.hypixel.net/%s".formatted(path));
            }
        }
    }

    @Shadow
    protected abstract void onMouseClick(Slot focusedSlot, int id, int i, SlotActionType slotActionType);
}