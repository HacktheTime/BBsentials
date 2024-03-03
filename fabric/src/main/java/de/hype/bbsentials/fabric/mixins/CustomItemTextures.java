package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

    @Shadow
    public abstract void drawGuiTexture(Identifier texture, int x, int y, int z, int width, int height);

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        String stackItemName = stack.getName().getString();
        if (BBsentials.funConfig.hub17To29Troll) {
            if (stack.getName().getString().equals("SkyBlock Hub #17")) {
                stack.setCustomName(Text.translatable("§aSkyBlock Hub #29"));
                stack.setCount(29);
            }
        }
        if (BBsentials.funConfig.hub29Troll) {
            if (stack.getName().getString().startsWith("SkyBlock Hub #")) {
                if (!stack.getName().getString().endsWith("29")) {
                    stack.setCustomName(Text.translatable("§aSkyBlock Hub #29 (" + stackItemName.replaceAll("\\D", "") + ")"));
                    stack.setCount(29);
                }
            }
        }
        if ((stack.getItem() == Items.EMERALD_BLOCK || stack.getItem() == Items.IRON_BLOCK) && BBsentials.visualConfig.showContributorPositionInCount) {
            NbtList list = stack.getNbt().getCompound("display").getList("Lore", NbtElement.STRING_TYPE);
            if (list.size() >= 20) {
                boolean found = false;
                for (int i = 20; i < list.size(); i++) {
                    String string = Text.Serialization.fromJson(list.get(i).asString()).getString();
                    if (string.contains("contributor")) {
                        int position = Integer.parseInt(string.replaceAll("\\D", ""));
                        stack.setCount(position);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    stack.setCount(-1);
                }
            }
        }
        for (CustomItemTexture itemTexture : BBsentials.customItemTextures.values()) {
            String nbtString = "";
            NbtCompound nbt = stack.getNbt();

            if (nbt != null) nbtString = nbt.toString();
            if (itemTexture.isItem(stackItemName, nbtString, stack)) {
                drawGuiTexture(new Identifier(itemTexture.nameSpace, itemTexture.renderTextureId), x, y, 16, 16);
                ci.cancel();
                return;
            }
        }
        if (BBsentials.splashConfig.showSmallestHub && (BBsentials.splashConfig.smallestHubName != null) && stack.getName().getString().equals(BBsentials.splashConfig.smallestHubName)) {
            drawGuiTexture(new Identifier("bbsentials:customitems/low_player_hub"), x, y, 16, 16);
            ci.cancel();
            return;
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
