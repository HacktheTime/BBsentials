package de.hype.bbsentials.mixins;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderItemMixin(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (BBsentials.config.highlightitem) {
            setCustomName(stack, BBsentials.connection.getItemName());
        }
    }

    @Unique
    private void setCustomName(ItemStack stack, String triggerName) {
        String temp = stack.getName().getString();
        if ((!temp.contains("Splash")) && temp.contains(triggerName)) {
            String tem2 = stack.getNbt().getString("Display");
            Chat.sendPrivateMessageToSelf(tem2);
            stack.setCustomName(Text.literal("§6(Bingo Splash) " + temp));
        }
    }
}
