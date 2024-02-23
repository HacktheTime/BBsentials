package de.hype.bbsentials.forge.mixin;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Shadow
    @Final
    private TextureManager textureManager;

    @Shadow
    protected abstract void draw(WorldRenderer p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int alpha);

//    @Inject(method = "renderItemOverlayIntoGUI", at = @At("RETURN"))
//    private void renderItemOverlayPost(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
//        Temphook.renderItemOverlayPost(fr, stack, xPosition, yPosition, text, ci);
//    }

    @Shadow protected abstract boolean isThereOneNegativeScale(ItemTransformVec3f itemTranformVec);

    @Inject(method = "renderItemIntoGUI", at = @At("HEAD"), cancellable = true)
    private void overrideItemTextureForHubs(ItemStack stack, int x, int y, CallbackInfo ci) {
        try {
            for (CustomItemTexture itemTexture : BBsentials.customItemTextures.values()) {
                if (itemTexture.isItem(stack.getDisplayName(), stack.getTagCompound().toString(), stack)) {
                    textureManager.bindTexture(new ResourceLocation(itemTexture.nameSpace, "textures/gui/sprites/" + itemTexture.renderTextureId + ".png"));
                    Gui.drawModalRectWithCustomSizedTexture(
                            x, y, 0, 0, 16, 16, 16, 16
                    );
                    ci.cancel();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
}
