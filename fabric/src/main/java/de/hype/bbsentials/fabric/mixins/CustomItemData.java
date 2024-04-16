package de.hype.bbsentials.fabric.mixins;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
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

    @Shadow
    public abstract boolean damage(int par1, Random par2, ServerPlayerEntity par3);

    @Shadow
    public abstract ItemStack setCustomName(Text par1);

    @Unique
    WeakReference<String> itemCountCustom;
    @Unique
    WeakReference<String> texturename;
    @Unique
    WeakReference<List<Text>> itemTooltip;


    @Inject(method = "<init>*", at = @At("RETURN"))
    public void BBsentials$customItemDataInit(ItemStack stack) {
        BBsentialsAll$reevaluate(stack);

    }

    @Override
    public List<Text> BBsentialsAll$getItemRenderTooltip() {
        return itemTooltip.get();
    }

    @Override
    public String BBsentialsAll$getCount() {
        return itemCountCustom.get();
    }

    @Override
    public String BBsentialsAll$getCustomItemTexture() {
        return texturename.get();
    }


    @Unique
    @Override
    public void BBsentialsAll$reevaluate(ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;

        RenderingDefinitions.RenderStackItemCheck data = new RenderingDefinitions.RenderStackItemCheck(stack);
        itemCountCustom = new WeakReference<>(data.getItemCount());
        texturename = new WeakReference<>(data.getTexturePath());
        itemTooltip = new WeakReference<>(data.getTextTooltip());
    }
}

