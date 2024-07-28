package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.FabricICusomItemDataAccess;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnreachableCode")
@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IItemRendererMixinAccess {
    @Final
    @Shadow
    private ItemModels models;
    @Shadow
    @Final
    private ItemColors colors;

    @Unique
    @Override
    public ItemModels BBsentials$getModels() {
        return models;
    }

    @Override
    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void BBsentials$getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        Item item = ((FabricICusomItemDataAccess) ((Object) stack)).BBsentialsAll$getRenderAsItem();
        if (item == null) return;
        BakedModel model = models.getModel(item);
        if (model == null) return;
        cir.setReturnValue(model);
    }
}
