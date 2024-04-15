package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.lang.ref.WeakReference;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class CustomItemData implements ICusomItemDataAccess {

    @Shadow
    public abstract void removeCustomName();

    @Shadow
    public abstract void removeSubNbt(String par1);

    @Unique
    WeakReference<String> ItemCountCustom;
    @Unique
    WeakReference<String> texturename;
    @Unique
    WeakReference<List<Text>> itemTooltip;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void BBsentials$customItemDataInit(ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        String texture = null;
        String customItemCount;


//        RenderingDefinitions.RenderStackItemCheck modified = RenderingDefinitions.check(stack);
//        ItemCountCustom = modified.getCount();

    }

    @Override
    public List<Text> BBsentialsAll$getItemRenderTooltip() {
        return itemTooltip.get();
    }

    @Override
    public String BBsentialsAll$getCount() {
        return ItemCountCustom.get();
    }

    @Override
    public String BBsentialsAll$getCustomItemTexture() {
        return texturename.get();
    }


}

