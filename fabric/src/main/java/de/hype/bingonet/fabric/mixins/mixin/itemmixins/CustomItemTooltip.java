package de.hype.bingonet.fabric.mixins.mixin.itemmixins;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.fabric.ModInitialiser;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.FabricICusomItemDataAccess;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import de.hype.bingonet.shared.constants.Islands;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnreachableCode")
@Mixin(HandledScreen.class)
public abstract class CustomItemTooltip<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {


    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    @Final
    protected T handler;

    protected CustomItemTooltip(Text title) {
        super(title);
    }


    //    @ModifyExpressionValue(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"))
//    private ItemStack BingoNet$modfiedItemStack(ItemStack original) {
//        if (original.getItem() == Items.EMERALD_BLOCK || original.getItem() == Items.IRON_BLOCK || original.getItem() == Items.PAPER) {
//            Utils.doBingoRankManipulations(original);
//        }
//        return original;
//    }
    @Inject(method = "<init>", at = @At("RETURN"))
    private void BingoNet$onConstructor(ScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        if (title.getString().equals("SkyBlock Hub Selector")) {
            BingoNet.temporaryConfig.lastServerIdUpdateDate = Instant.now();
            BingoNet.temporaryConfig.serverIdToHubNumber.clear();
            BingoNet.executionService.schedule(() -> {
                int lowestYetAmount = Integer.MAX_VALUE;
                ItemStack lowestYetHub = null;
                for (Slot slot : handler.slots) {
                    ItemStack stack = slot.getStack();
                    if (stack.getItem() == Items.BLACK_STAINED_GLASS_PANE) continue;
                    if (!stack.getName().getString().startsWith("SkyBlock Hub")) continue;
                    try {
                        FabricICusomItemDataAccess access = (FabricICusomItemDataAccess) (Object) stack;
                        List<Text> texts = access.BingoNetAll$getItemRenderTooltip();
                        String serverid = "";
                        int playerCount = -1;
                        int hubNumber;
                        boolean full = false;

                        for (Text text : texts) {
                            String line = text.getString();
                            if (line.matches("Players: \\d+/\\d+")) {
                                playerCount = Integer.parseInt(line.replace("Players:", "").split("/")[0].trim());
                                if (line.equals("Players: " + playerCount + "/" + playerCount)) full = true;
                            }
                            else if (line.matches("Server: .*")) {
                                serverid = line.replace("Server:", "").trim();
                            }
                        }
                        hubNumber = stack.getCount();
                        if (!serverid.isEmpty()) {
                            String finalServerid = serverid;
                            BingoNet.temporaryConfig.serverIdToHubNumber.compute(Islands.HUB, (island, map) -> {
                                if (map == null) map = new java.util.HashMap<>();
                                map.put(finalServerid, hubNumber);
                                return map;
                            });
                        }

                        if (playerCount < lowestYetAmount && !full) {
                            lowestYetAmount = playerCount;
                            lowestYetHub = stack;
                        }
                    } catch (Exception e) {
                    }
                }
                BingoNet.splashConfig.smallestHubName = lowestYetHub.getName().getString();
                ((ICusomItemDataAccess) ((Object) lowestYetHub)).BingoNetAll$reevaluate();
            }, 30, TimeUnit.MILLISECONDS);
        }
        BingoNet.executionService.schedule(() -> {
            ModInitialiser.tutorialManager.openedInventory((HandledScreen<T>) (Object) this);
        }, 40, TimeUnit.MILLISECONDS);
    }

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("RETURN"))
    public void BingoNet$mouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot == null) {
            return;
        }
        ItemStack stack = slot.getStack();
        ModInitialiser.tutorialManager.clickedItemInInventory(stack, slotId, title.getString());
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void BingoNet$onClose(CallbackInfo ci) {
        BingoNet.splashConfig.smallestHubName = null;
    }

}
