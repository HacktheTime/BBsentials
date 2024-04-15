package de.hype.bbsentials.fabric.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RenderingDefinitions {
    private static Integer renderDefIdCounter = 0;
    public final Integer renderDefId = renderDefIdCounter++;
    /**
     * @param stack Use this stack for matching conditions based on your needs. However do not edit it since the data is copied. This is due too some things kicking you otherwise.
     * @param texts the Custom item tooltip.
     * @return the tooltip you want to be displayed when hovering the item
     */
    Map<Integer, RenderingDefinitions> defs = new HashMap<>();

    public RenderingDefinitions() {
        defs.put(renderDefId, this);
    }

    public boolean isRegistered() {
        return defs.get(renderDefId) != null;
    }

    public void removeFromPool() {
        defs.remove(renderDefId);
    }

    /**
     * Try to filter out non matching items out as soon as you can before you do the intensive stuff since it takes more performance otherwise!
     *
     * @return return value defines whether you want to stop after the check
     */
    public abstract boolean modifyItem(ItemStack stack, RenderStackItemCheck check, String itemName);

    public class RenderStackItemCheck {
        private String texturePath = null;
        private Integer itemcount = null;
        private List<Text> texts = new ArrayList<>();
        private final ItemStack stack;

        public RenderStackItemCheck(ItemStack stack) {
            this.stack = stack;
            String itemName = stack.getName().getString();
            for (RenderingDefinitions def : defs.values()) {
                if (def.modifyItem(stack, this, itemName)) {
                    return;
                }
            }
        }

        public List<Text> getText() {
            if (texts != null) return texts;
            NbtCompound b = stack.getNbt();
            if (b == null) return new ArrayList<>();
            NbtCompound b1 = b.getCompound("display");
            if (b1 == null) return new ArrayList<>();
            NbtList list = b1.getList("Lore", NbtElement.STRING_TYPE);
            List<Text> text = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Text.Serialization.fromJson(list.get(i).asString());
            }
            texts = text;
            return text;
        }

        public Integer getItemcount() {
            if (itemcount == null) return stack.getCount();
            return itemcount;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public void setItemcount(Integer itemcount) {
            this.itemcount = itemcount;
        }

        public void setTexts(List<Text> texts) {
            this.texts = texts;
        }

        public void setTexturePath(String texturePath) {
            this.texturePath = texturePath;
        }

        public void renderAsItem(Item item) {
            texturePath = Registries.ITEM.getId(item).getPath();
        }
    }
}