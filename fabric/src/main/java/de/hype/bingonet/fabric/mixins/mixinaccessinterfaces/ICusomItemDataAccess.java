package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import de.hype.bingonet.client.common.mclibraries.interfaces.Text;
import de.hype.bingonet.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bingonet.shared.constants.VanillaItems;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface ICusomItemDataAccess {
    String BingoNetAll$getCount();

    String BingoNetAll$getCustomItemTexture();

    void BingoNetAll$reevaluate();

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BingoNetAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition);

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BingoNetAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition, Boolean override);

    @Unique
    @SuppressWarnings("UnreachableCode")
    void BingoNetAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition, boolean override);


    boolean BingoNet$areEqualExtension(ICusomItemDataAccess aRight);

    String getTexturename();

    void setTexturename(String texturename);

    boolean isOverride();

    void setOverride(boolean override);

    boolean isNotInitialised();

    void setNotInitialised(boolean notInitialised);

    List<de.hype.bingonet.client.common.mclibraries.interfaces.Text> getItemTooltip();

    void setItemTooltip(List<de.hype.bingonet.client.common.mclibraries.interfaces.Text> value);

    String getItemCountCustom();

    void setItemCountCustom(String itemCountCustom);

    List<Text> getCustomAppliedTooltip();

    VanillaItems getVanillaRenderasItem();

    void setVanillaRenderasItem(VanillaItems renderasItem);

}

