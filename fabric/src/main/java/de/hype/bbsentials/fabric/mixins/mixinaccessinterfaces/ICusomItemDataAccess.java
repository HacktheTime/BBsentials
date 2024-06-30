package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import de.hype.bbsentials.fabric.mixins.helperclasses.RenderingDefinitions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface ICusomItemDataAccess {
    List<Text> BBsentialsAll$getItemRenderTooltip();

    String BBsentialsAll$getCount();

    String BBsentialsAll$getCustomItemTexture();

    Item BBsentialsAll$getRenderAsItem();

    void BBsentialsAll$reevaluate();

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BBsentialsAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition);

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BBsentialsAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition, Boolean override);

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BBsentialsAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition, boolean override);

    ItemStack BBsentials$getAsItemStack();

    boolean BBsentials$areEqualExtension(ICusomItemDataAccess aRight);
}
