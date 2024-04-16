package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class CustomItemData implements ICusomItemDataAccess {

    @Unique
    String itemCountCustom = null;
    @Unique
    String texturename = null;
    @Unique
    List<Text> itemTooltip = null;
    boolean notInitialised = true;

    @Shadow
    public abstract String toString();

    @Override
    public List<Text> BBsentialsAll$getItemRenderTooltip() {
        if (notInitialised) BBsentialsAll$reevaluate();
        return itemTooltip;
    }

    @Override
    public String BBsentialsAll$getCount() {
        if (notInitialised) BBsentialsAll$reevaluate();
        return itemCountCustom;
    }

    @Override
    public String BBsentialsAll$getCustomItemTexture() {
        if (notInitialised) BBsentialsAll$reevaluate();
        return texturename;
    }


    @Unique
    @SuppressWarnings("UnreachableCode")
    @Override
    public void BBsentialsAll$reevaluate() {
        notInitialised = true;
        ItemStack stack = BBsentials$getAsItemStack();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        RenderingDefinitions.RenderStackItemCheck data = new RenderingDefinitions.RenderStackItemCheck(stack);
        itemCountCustom = data.getItemCount();
        texturename = data.getTexturePath();
        itemTooltip = data.getTextTooltip();
    }

    @Override
    @Unique
    public ItemStack BBsentials$getAsItemStack() {
        return (ItemStack) (Object) this;
    }

}

