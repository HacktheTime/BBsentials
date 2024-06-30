package de.hype.bbsentials.fabric.mixins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hype.bbsentials.fabric.tutorial.nodes.ClickItemNode;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow
    @Final
    private List<ScreenHandlerListener> listeners;

    @Shadow public abstract void sendContentUpdates();

    @ModifyExpressionValue(
            method = "updateTrackedSlot",
            at = @At(value = "FIELD", target = "Lnet/minecraft/screen/ScreenHandler;listeners:Ljava/util/List;")
    )
    private List<ScreenHandlerListener> BBsentials$sameContentList(List<ScreenHandlerListener> original) {
        return new ArrayList<>(listeners);
    }

    @Inject(method = "addListener", at = @At("HEAD"), cancellable = true)
    private void BBsentials$addListener(ScreenHandlerListener listener, CallbackInfo ci) {
        if (listener instanceof ClickItemNode.ClickItemNodeHanlderListener) {
            listeners.add(listener);
            ci.cancel();
        }
    }

    @Inject(method = "updateSlotStacks", at = @At("RETURN"), cancellable = true)
    private void BBsentials$updateCallback(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        sendContentUpdates();
    }
}
