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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

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

    @ModifyVariable(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), argsOnly = true)
    private ItemStack modifyItemStack(ItemStack stack) {
        String stackItemName = stack.getName().getString();
        Integer stackCount = stack.getCount();
//        if (BBsentials.funConfig.hub17To29Troll) {
//            if (stackItemName.equals("SkyBlock Hub #17")) {
//                stack.setCustomName(Text.translatable("§aSkyBlock Hub #29"));
//            }
//            stackCount = 29;
//        }
//        if (BBsentials.funConfig.hub29Troll) {
//            if (stackItemName.startsWith("SkyBlock Hub #")) {
//                if (!stack.getName().getString().endsWith("29")) {
//                    stack.setCustomName(Text.translatable("§aSkyBlock Hub #29 (" + stackItemName.replaceAll("\\D", "") + ")"));
//                    stackCount = 29;
//                }
//                stackCount=29;
//            }
//        }
        if ((stack.getItem() == Items.EMERALD_BLOCK || stack.getItem() == Items.IRON_BLOCK) && BBsentials.visualConfig.showContributorPositionInCount) {
            NbtList list = stack.getNbt().getCompound("display").getList("Lore", NbtElement.STRING_TYPE);
            if (list.size() >= 2 && Text.Serialization.fromJson(list.get(0).asString()).getString().equals("Community Goal")) {
                if (list.size() >= 20) {
                    boolean found = false;
                    for (int i = 20; i < list.size(); i++) {
                        String string = Text.Serialization.fromJson(list.get(i).asString()).getString();
                        if (string.contains("contributor")) {
                            int position = Integer.parseInt(string.replaceAll("\\D", ""));
                            stack.setCount(position);
                            found = true;
                            if (position == 1) {
                                if (stack.getItem() == Items.IRON_BLOCK)
                                    stack = new ItemStack(RegistryEntry.of(Items.DIAMOND_BLOCK), 1, Optional.of(stack.getNbt()));
                                if (stack.getItem() == Items.EMERALD_BLOCK)
                                    stack = new ItemStack(RegistryEntry.of(Items.NETHERITE_BLOCK), 1, Optional.of(stack.getNbt()));
                                stackCount = 1;
                            }
                            break;
                        }
                    }
                    if (!found && BBsentials.funConfig.show404IfNotPositioned) {
                        stackCount = 404;
                    }
                }
            }
        }
        return stack.copyWithCount(stackCount);
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        String stackItemName = stack.getName().getString();
        boolean cancel = false;
        for (CustomItemTexture itemTexture : BBsentials.customItemTextures.values()) {
            String nbtString = "";
            NbtCompound nbt = stack.getNbt();

            if (nbt != null) nbtString = nbt.toString();
            if (itemTexture.isItem(stackItemName, nbtString, stack)) {
                drawGuiTexture(new Identifier(itemTexture.nameSpace, itemTexture.renderTextureId), x, y, 16, 16);
                cancel = true;
                break;
            }
        }
        if (BBsentials.splashConfig.showSmallestHub && (BBsentials.splashConfig.smallestHubName != null) && stack.getName().getString().equals(BBsentials.splashConfig.smallestHubName)) {
            drawGuiTexture(new Identifier("bbsentials:customitems/low_player_hub"), x, y, 16, 16);
            cancel = true;
        }
        if (cancel) ci.cancel();
    }
}
