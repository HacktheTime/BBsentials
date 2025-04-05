package de.hype.bingonet.fabric.mixins.mixin;

import com.google.gson.JsonParser;
import de.hype.bingonet.client.common.SystemUtils;
import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.client.SplashManager;
import de.hype.bingonet.fabric.ModInitialiser;
import de.hype.bingonet.shared.constants.Collections;
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
import net.minecraft.util.ClickType;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Mixin(HandledScreen.class)
public abstract class InventoryKeyBinds<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

    @Shadow
    @Final
    protected T handler;

    @Shadow
    public abstract void close();

    @Nullable
    @Shadow
    protected Slot focusedSlot;

    protected InventoryKeyBinds(Text title) {
        super(title);
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (focusedSlot != null && focusedSlot.getStack().getItem() != Items.AIR) {
                if (keyCode == 258) clickSlot(focusedSlot, SlotActionType.QUICK_MOVE, ClickType.RIGHT);
                else if (keyCode == KeyBindingHelper.getBoundKeyOf(ModInitialiser.openWikiKeybind).getCode()) {
                    NbtComponent customData = focusedSlot.getStack().get(DataComponentTypes.CUSTOM_DATA);
                    if (customData == null) return;
                    NbtCompound data = customData.copyNbt();
                    String id = data.getString("id");
                    String lowercaseID = id.toLowerCase();
                    String path = "";
                    if (lowercaseID.equals("enchanted_book")) {
                        path = WordUtils.capitalize((new ArrayList<>(data.getCompound("enchantments").getKeys()).get(0).replace("ultimate_", "") + "_Enchantment").replace("_", " ")).replace(" ", "_");
                    } else if (lowercaseID.equals("pet")) {
                        path = WordUtils.capitalize((JsonParser.parseString(data.getString("petInfo")).getAsJsonObject().get("type").getAsString().toLowerCase() + "_Pet").replace("_", " ")).replace(" ", "_");
                    } else if (lowercaseID.equals("potion")) {
                        path = WordUtils.capitalize((data.getString("potion").toLowerCase().replace("xp", "XP") + "_Potion").replace("_", " ")).replace(" ", "_");
                    } else if (lowercaseID.equals("rune")) {
                        path = WordUtils.capitalize((new ArrayList<>(data.getCompound("runes").getKeys()).get(0).toLowerCase() + "_Rune").replace("_", " ")).replace(" ", "_");
                    } else {
                        path = id;
                    }
                    SystemUtils.openInBrowser("https://wiki.hypixel.net/%s".formatted(path));
                } else if (keyCode == KeyBindingHelper.getBoundKeyOf(ModInitialiser.promptKeyBind).getCode()) {
                    if (getTitle().getString().endsWith("Selector")) {
                        if (BingoNet.dataStorage.getServerJoinTime().plus(3, ChronoUnit.SECONDS).isBefore(Instant.now())) {
                            for (SplashManager.DisplaySplash value : SplashManager.splashPool.values()) {
                                if (value.serverID != null) {
                                    for (Slot slot : handler.slots) {
                                        for (Text line : slot.getStack().get(DataComponentTypes.LORE).lines()) {
                                            if (line.getString().matches("Server: %s".formatted(value.serverID))) {
                                                clickSlot(slot, SlotActionType.PICKUP, ClickType.LEFT);
                                                return;
                                            }
                                        }
                                    }
                                } else if (value.hubSelectorData != null) {
                                    for (Slot slot : handler.slots) {
                                        if (slot.getStack().getName().getString().endsWith("#%s".formatted(value.hubSelectorData.hubNumber))) {
                                            clickSlot(slot, SlotActionType.PICKUP, ClickType.LEFT);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        NbtComponent customData = focusedSlot.getStack().get(DataComponentTypes.CUSTOM_DATA);
                        if (customData == null) return;
                        NbtCompound data = customData.copyNbt();
                        String id = data.getString("id");
                        BingoNet.executionService.execute(() -> {
                            for (Collections value : Collections.values()) {
                                if (value.getId().equalsIgnoreCase(id)) {
                                    String minionId = value.getMinionID();
                                    if (minionId != null)
                                        BingoNet.sender.addSendTask("/viewrecipe %s".formatted(minionId), 0);
                                }
                                return;
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError("BingoNet > (please report this) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void clickSlot(Slot slot, SlotActionType slotActionType, ClickType clickType) {
        int button = switch (clickType) {
            case LEFT -> 0;
            case RIGHT -> 1;
        };
        client.interactionManager.clickSlot(this.handler.syncId, slot.id, button, slotActionType, this.client.player);
    }
}