package de.hype.bbsentials.fabric.mixins.mixin.itemmixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hype.bbsentials.fabric.VanillaRegistry;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import de.hype.bbsentials.shared.constants.VanillaItems;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnreachableCode")
@Mixin(ItemModels.class)
public abstract class CustomRenderAsItemMixin {

    @ModifyExpressionValue(
            method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;", ordinal = 0)
    )
    private Object BBsentials$overrideGetModel(Object original, ItemStack stack) {
        if (stack == null) return original;
        ICusomItemDataAccess data = (ICusomItemDataAccess) (Object) stack;
        VanillaItems renderAsItem = data.getVanillaRenderasItem();
        if (renderAsItem != null) {
            return VanillaRegistry.get(renderAsItem).getComponents().get(DataComponentTypes.ITEM_MODEL);
        }
        return original;
    }
}