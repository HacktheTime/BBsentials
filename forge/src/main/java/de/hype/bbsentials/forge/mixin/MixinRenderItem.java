package de.hype.bingonet.forge.mixin;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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

//    @Inject(method = "renderItemIntoGUI", at = @At("HEAD"), cancellable = true)
//    private void overrideItemTextureForHubs(ItemStack stack, int x, int y, CallbackInfo ci) {
//        try {
//            for (CustomItemTexture itemTexture : BingoNet.customItemTextures.values()) {
//                if (itemTexture.isItem(stack.getDisplayName(), stack.getTagCompound().toString())) {
//                    textureManager.bindTexture(new ResourceLocation(itemTexture.nameSpace, "textures/gui/sprites/" + itemTexture.renderTextureId + ".png"));
//                    Gui.drawModalRectWithCustomSizedTexture(
//                            x, y, 0, 0, 16, 16, 16, 16
//                    );
//                    ci.cancel();
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Chat.sendPrivateMessageToSelfError(e.getMessage());
//        }
//    }
}
