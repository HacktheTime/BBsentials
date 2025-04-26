package de.hype.bingonet.fabric.mixins.mixin.itemmixins;

import de.hype.bingonet.fabric.VanillaRegistry;
import de.hype.bingonet.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.FabricICusomItemDataAccess;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import de.hype.bingonet.shared.constants.VanillaItems;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.component.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class CustomItemData implements FabricICusomItemDataAccess {

    @Unique
    String itemCountCustom = null;
    @Unique
    String texturename = null;
    @Unique
    List<de.hype.bingonet.fabric.Text> itemTooltip = null;
    @Unique
    boolean notInitialised = true;
    @Unique
    boolean override;


    @Shadow
    public abstract String toString();

    @Shadow
    @Final
    private MergedComponentMap components;

    @Shadow
    public abstract ComponentMap getComponents();

    @Shadow
    @Nullable
    public abstract <T> T remove(ComponentType<? extends T> type);

    @Override
    public List<Text> BingoNetAll$getItemRenderTooltip() {
        if (notInitialised) BingoNetAll$reevaluate();
        return itemTooltip.stream().map(de.hype.bingonet.fabric.Text::getAsText).toList();
    }

    @Override
    public String BingoNetAll$getCount() {
        if (notInitialised) BingoNetAll$reevaluate();
        return itemCountCustom;
    }

    @Override
    public String BingoNetAll$getCustomItemTexture() {
        if (notInitialised) BingoNetAll$reevaluate();
        return texturename;
    }

    @Unique
    @SuppressWarnings("UnreachableCode")
    @Override
    public void BingoNetAll$reevaluate() {
        notInitialised = false;
        ItemStack stack = BingoNet$getAsItemStack();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        RenderingDefinitions.RenderStackItemCheck data = new RenderingDefinitions.RenderStackItemCheck(new de.hype.bingonet.fabric.ItemStack(stack));
        BingoNetAll$setRenderingDefinition(data, false);
    }

    @Unique
    @SuppressWarnings("UnreachableCode")
    @Override
    public synchronized void BingoNetAll$setRenderingDefinition(RenderingDefinitions.RenderStackItemCheck definition, boolean override) {
        if (!override && this.override) {
            return;
        }
        else if (override) {
            this.override = true;
        }
        notInitialised = false;
        setRenderasItem(VanillaRegistry.get(definition.getRenderAsItem()));
        itemCountCustom = definition.getItemCount();
        texturename = definition.getTexturePath();
        itemTooltip = definition.getTextTooltip().stream().map(v -> (de.hype.bingonet.fabric.Text) v).toList();
    }

    @Override
    @Unique
    public ItemStack BingoNet$getAsItemStack() {
        return (ItemStack) (Object) this;
    }

    @Unique
    public boolean BingoNet$areEqualExtension(ICusomItemDataAccess right) {
        CustomItemData right1 = (CustomItemData) (Object) right;
        if (right1.override != override) return false;
        if (!Objects.equals(right1.texturename, texturename)) return false;
        if (right1.itemTooltip != itemTooltip) return false;
        if (!Objects.equals(right1.getComponents().get(DataComponentTypes.ITEM_MODEL), this.getComponents().get(DataComponentTypes.ITEM_MODEL)))
            return false;
        return true;
    }

    @Override
    public String getTexturename() {
        return texturename;
    }

    @Override
    public void setTexturename(String texturename) {
        this.texturename = texturename;
    }

    @Override
    public boolean isOverride() {
        return override;
    }

    @Override
    public void setOverride(boolean override) {
        this.override = override;
    }



    @Override
    public void setRenderasItem(Item renderasItem) {
        if (renderasItem == null) {
            components.remove(DataComponentTypes.ITEM_MODEL);
        } else
            components.applyChanges(ComponentChanges.builder().add(DataComponentTypes.ITEM_MODEL, renderasItem.getComponents().get(DataComponentTypes.ITEM_MODEL)).build());
    }

    @Override
    public void setVanillaRenderasItem(VanillaItems renderasItem) {
        setRenderasItem(VanillaRegistry.get(renderasItem));
    }


    @Override
    public boolean isNotInitialised() {
        return notInitialised;
    }

    @Override
    public void setNotInitialised(boolean notInitialised) {
        this.notInitialised = notInitialised;
    }

    @Override
    public List<de.hype.bingonet.client.common.mclibraries.interfaces.Text> getItemTooltip() {
        return itemTooltip.stream().map(v -> (de.hype.bingonet.client.common.mclibraries.interfaces.Text) v).toList();
    }

    @Override
    public void setItemTooltip(List<de.hype.bingonet.client.common.mclibraries.interfaces.Text> value) {
        this.itemTooltip = value.stream().map(v -> (de.hype.bingonet.fabric.Text) v).toList();
    }

    @Override
    public String getItemCountCustom() {
        return itemCountCustom;
    }

    @Override
    public void setItemCountCustom(String itemCountCustom) {
        this.itemCountCustom = itemCountCustom;
    }

}

