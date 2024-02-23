package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class CustomItemTextures {
    @Shadow
    public abstract void drawGuiTexture(Identifier texture, int x, int y, int width, int height);

    @Shadow
    public abstract int getScaledWindowWidth();

    @Shadow
    public abstract void drawTooltip(TextRenderer textRenderer, Text text, int x, int y);

    @Shadow
    public abstract int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        for (CustomItemTexture itemTexture : BBsentials.customItemTextures.values()) {
            String nbtString = "";
            NbtCompound nbt = stack.getNbt();
            String stackItemName = stack.getName().getString();
            if (nbt != null) nbtString = nbt.toString();
            if (itemTexture.isItem(stackItemName, nbtString, stack)) {
                drawGuiTexture(new Identifier(itemTexture.nameSpace, itemTexture.renderTextureId), x, y, 16, 16);
                ci.cancel();
            }
            if (stackItemName.equals(BBsentials.splashConfig.smallestHubName != null && BBsentials.splashConfig.showSmallestHub)) {
                drawText(MinecraftClient.getInstance().textRenderer, Text.of("§aThe smallest Hub is currently " + BBsentials.splashConfig.smallestHubName), 10, 100, 16777215, false);
            }
        }
//        if (stack.getItem() == Items.POTION) {
//            try {
//                String potionEffect = stack.getNbt().getCompound("ExtraAttributes").getString("potion");
//                if (potionEffect.equals("foraging_xp_boost")) {
//                }
//            } catch (Exception e) {
//
//            }
//        }
    }
}
