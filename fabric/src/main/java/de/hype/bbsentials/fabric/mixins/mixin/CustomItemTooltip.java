package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(HandledScreen.class)
public abstract class CustomItemTooltip<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {


    protected CustomItemTooltip(Text title) {
        super(title);
    }


//    @ModifyExpressionValue(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"))
//    private ItemStack BBsentials$modfiedItemStack(ItemStack original) {
//        if (original.getItem() == Items.EMERALD_BLOCK || original.getItem() == Items.IRON_BLOCK || original.getItem() == Items.PAPER) {
//            Utils.doBingoRankManipulations(original);
//        }
//        return original;
//    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void BBsentials$onConstructor(ScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        if (title.getString().equals("SkyBlock Hub Selector")) {
            BBsentials.executionService.schedule(() -> {
                int lowestYetAmount = Integer.MAX_VALUE;
                ItemStack lowestYetHub = null;
                for (Slot slot : handler.slots) {
                    ItemStack stack = slot.getStack();
                    if (stack.getItem() == Items.BLACK_STAINED_GLASS_PANE) continue;
                    try {
                        String data = stack.getNbt().getCompound("display").getList("Lore", NbtElement.STRING_TYPE).get(0).asString();
                        int amount = Integer.parseInt(Text.Serialization.fromJson(data).getString().replace("Players:", "").split("/")[0].trim());
                        if (amount < lowestYetAmount) {
                            lowestYetAmount = amount;
                            lowestYetHub = stack;
                        }
                    } catch (Exception e) {
                    }
                }
                BBsentials.splashConfig.smallestHubName = lowestYetHub.getName().getString();
                ((ICusomItemDataAccess) ((Object) lowestYetHub)).BBsentialsAll$reevaluate();
            }, 30, TimeUnit.MILLISECONDS);
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void BBsentials$onClose(CallbackInfo ci) {
        BBsentials.splashConfig.smallestHubName = null;
    }
}
