package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public interface FabricICusomItemDataAccess extends ICusomItemDataAccess {
    List<Text> BingoNetAll$getItemRenderTooltip();

    Item BingoNetAll$getRenderAsItem();

    ItemStack BingoNet$getAsItemStack();

    Item getRenderasItem();

    void setRenderasItem(Item renderasItem);

}
