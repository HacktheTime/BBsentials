package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public interface FabricICusomItemDataAccess extends ICusomItemDataAccess {
    List<Text> BBsentialsAll$getItemRenderTooltip();

    Item BBsentialsAll$getRenderAsItem();

    ItemStack BBsentials$getAsItemStack();

    Item getRenderasItem();

    void setRenderasItem(Item renderasItem);

}
