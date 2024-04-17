package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.World;
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
    @Unique
    boolean notInitialised = true;
    @Unique
    Item renderasItem = null;

    @Shadow
    public abstract String toString();

    @Shadow
    public abstract ItemStack finishUsing(World par1, LivingEntity par2);

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

    @Override
    public Item BBsentialsAll$getRenderAsItem() {
        if (notInitialised) BBsentialsAll$reevaluate();
        return renderasItem;
    }


    @Unique
    @SuppressWarnings("UnreachableCode")
    @Override
    public void BBsentialsAll$reevaluate() {
        notInitialised = false;
        ItemStack stack = BBsentials$getAsItemStack();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        RenderingDefinitions.RenderStackItemCheck data = new RenderingDefinitions.RenderStackItemCheck(stack);
        renderasItem = data.getRenderAsItem();
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

