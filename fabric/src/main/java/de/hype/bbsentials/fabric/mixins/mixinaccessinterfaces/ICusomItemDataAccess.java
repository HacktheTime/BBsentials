package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import de.hype.bbsentials.client.common.mclibraries.interfaces.Text;
import de.hype.bbsentials.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bbsentials.shared.constants.VanillaItems;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface ICusomItemDataAccess {
    String BBsentialsAll$getCount();

    String BBsentialsAll$getCustomItemTexture();

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


    boolean BBsentials$areEqualExtension(ICusomItemDataAccess aRight);

    String getTexturename();

    void setTexturename(String texturename);

    boolean isOverride();

    void setOverride(boolean override);

    boolean isNotInitialised();

    void setNotInitialised(boolean notInitialised);

    List<de.hype.bbsentials.client.common.mclibraries.interfaces.Text> getItemTooltip();

    void setItemTooltip(List<de.hype.bbsentials.client.common.mclibraries.interfaces.Text> value);

    String getItemCountCustom();

    void setItemCountCustom(String itemCountCustom);

    List<Text> getCustomAppliedTooltip();

    VanillaItems getVanillaRenderasItem();

    void setVanillaRenderasItem(VanillaItems renderasItem);

}

