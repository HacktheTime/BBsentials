package de.hype.bbsentials.fabric.mixins.helperclasses;

import de.hype.bbsentials.client.common.api.Formatting;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.SplashManager;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICusomItemDataAccess;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.time.Instant;
import java.util.*;

public abstract class RenderingDefinitions {
    /**
     * @param stack Use this stack for matching conditions based on your needs. However do not edit it since the data is copied. This is due too some things kicking you otherwise.
     * @param texts the Custom item tooltip.
     * @return the tooltip you want to be displayed when hovering the item
     */
    public static Map<Integer, RenderingDefinitions> defsBlocking = new HashMap<>();
    public static Map<Integer, RenderingDefinitions> defsNonBlocking = new HashMap<>();

    private static Integer renderDefIdCounter = 0;
    public final Integer renderDefId = renderDefIdCounter++;

    public RenderingDefinitions() {
        defsBlocking.put(renderDefId, this);
    }

    /**
     * @param blocking is the information youre modifying final or may it be process from something else as well?
     */
    public RenderingDefinitions(boolean blocking) {
        if (blocking) defsBlocking.put(renderDefId, this);
        else defsNonBlocking.put(renderDefId, this);
    }

    public static void clearAndInitDefaults() {
        defsBlocking.clear();
        defsNonBlocking.clear();
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            modifyItemTooltip(stack, context, lines);
        });
        new RenderingDefinitions() {
            @Override
            public boolean modifyItem(ItemStack stack, RenderStackItemCheck check, String itemName) {
                {
                    if (!itemName.startsWith("SkyBlock Hub")) return false;
                    String serverid = "";
                    int playerCount = -1;
                    int hubNumber;
                    boolean full = false;

                    List<Text> texts = check.getTextTooltip();
                    for (Text text : texts) {
                        String line = text.getString();
                        if (line.matches("Players: \\d+/\\d+")) {
                            playerCount = Integer.parseInt(line.replace("Players:", "").split("/")[0].trim());
                            if (line.equals("Players: " + playerCount + "/" + playerCount)) full = true;
                        }
                        else if (line.matches("Server: .*")) {
                            serverid = line.replace("Servers: ", "").trim();
                        }
                    }
                    hubNumber = stack.getCount();
                    if (!SplashManager.splashPool.isEmpty()) {
                        for (SplashManager.DisplaySplash value : SplashManager.splashPool.values()) {
                            if (value.serverID.equals(serverid)) {
                                if (value.receivedTime.isAfter(Instant.now().minusSeconds(20))) {
                                    if (full) check.texturePath = "customitems/splash_hub_full";
                                    else check.texturePath = "customitems/splash_hub";
                                    return true;
                                }
                            }
                            else if (value.serverID.isEmpty()) {
                                if (value.hubNumber == hubNumber) {
                                    if (full) check.texturePath = "customitems/splash_hub_full";
                                    else check.texturePath = "customitems/splash_hub";
                                    return true;
                                }
                            }
                        }
                    }
                    if (BBsentials.splashConfig.showSmallestHub && (BBsentials.splashConfig.smallestHubName != null)) {
                        if (itemName.equals(BBsentials.splashConfig.smallestHubName)) {
                            check.setTexturePath("bbsentials:customitems/low_player_hub");
                            return true;
                        }
                    }
                    if (BBsentials.funConfig.hub29Troll) {
                        check.setItemStackName(Text.translatable("§aSkyBlock Hub #29 (" + itemName.replaceAll("\\D", "") + ")"));
                        check.setItemCount(29);
                    }
                    else if (BBsentials.funConfig.hub17To29Troll) {
                        if (hubNumber == 17) {
                            check.setItemStackName(Text.translatable("§aSkyBlock Hub #29"));
                            check.setItemCount(29);
                        }
                    }
                    return false;
                }
            }
        };
        if (BBsentials.generalConfig.hasBBRoles("splasher")) {
            new RenderingDefinitions() {
                @Override
                public boolean modifyItem(ItemStack stack, RenderStackItemCheck check, String itemName) {
                    if (!BBsentials.splashConfig.xpBoostHighlight) return false;
                    if (itemName.endsWith("Potion")) {
                        if (itemName.contains("XP Boost")) {
                            NbtCompound c1 = stack.getNbt();
                            if (c1 == null) return false;
                            NbtCompound c2 = c1.getCompound("ExtraAttributes");
                            if (c2 == null) return false;
                            int t1 = c2.getInt("potion_level");
                            String countString = "T" + t1;
                            check.setItemCount(countString);
                            if (itemName.startsWith("Farming")) check.renderAsItem(Items.STONE_HOE);
                            if (itemName.startsWith("Foraging")) check.renderAsItem(Items.STONE_AXE);
                            if (itemName.startsWith("Fishing")) check.renderAsItem(Items.FISHING_ROD);
                            if (itemName.startsWith("Mining")) check.renderAsItem(Items.EMERALD_BLOCK);
                            if (itemName.startsWith("Alchemy")) check.renderAsItem(Items.BREWING_STAND);
                            if (itemName.startsWith("Enchanting")) check.renderAsItem(Items.ENCHANTING_TABLE);
                            if (itemName.startsWith("Combat")) check.renderAsItem(Items.STONE_SWORD);
                            return false;
                        }
                    }
                    return false;
                }
            };
        }
        new RenderingDefinitions(false) {
            @Override
            public boolean modifyItem(ItemStack stack, RenderStackItemCheck check, String itemName) {
                if (!BBsentials.developerConfig.hypixelItemInfo) return false;
                try {
                    NbtCompound nbt = stack.getOrCreateNbt();
                    NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
                    List<Text> itemTooltip = check.getTextTooltip();
                    Set<String> keys = extraAttributes.getKeys();
                    for (String key : keys) {
                        if (key.equals("enchantments")) continue;
                        if (key.equals("timestamp")) {
                            Long stamp = extraAttributes.getLong(key);
                            itemTooltip.add(Text.of("timestamp(Creation Date): " + stamp + "(" + Instant.ofEpochMilli(stamp) + ")"));
                            continue;
                        }
                        itemTooltip.add(Text.of(key + ": " + extraAttributes.get(key)));
                    }
                    stack.getNbt().putBoolean("addedDebug", true);
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
        new RenderingDefinitions() {
            @Override
            public boolean modifyItem(ItemStack stacks, RenderStackItemCheck check, String itemName) {
                Item stackItem = stacks.getItem();
                if (BBsentials.visualConfig.showContributorPositionInCount && (stackItem == Items.EMERALD_BLOCK || stackItem == Items.IRON_BLOCK)) {
                    List<Text> text = check.getTextTooltip();
                    if (text.size() >= 20) {
                        String line = text.get(1).getString();
                        if (!line.equals("Community Goal")) return false;
                        String[] temp = itemName.split(" ");
                        String tierString = temp[temp.length - 1];
                        switch (tierString) {
                            case "V", "5" -> check.renderAsItem(Items.NETHERITE_BLOCK);
                            case "IV", "4" -> check.renderAsItem(Items.DIAMOND_BLOCK);
                            case "III", "3" -> check.renderAsItem(Items.GOLD_BLOCK);
                            case "II", "2" -> check.renderAsItem(Items.IRON_BLOCK);
                            case "I", "1" -> check.renderAsItem(Items.COPPER_BLOCK);
                            default -> check.renderAsItem(Items.REDSTONE_BLOCK);
                        }
                        Integer position = null;
                        Double topPos = null;
                        for (int i = 20; i < text.size(); i++) {

                            line = text.get(i).getString();
                            if (line.contains("contributor")) {
                                position = Integer.parseInt(line.replaceAll("\\D", ""));
                            }
                            if (line.contains("Top")) {
                                topPos = Double.parseDouble(line.replaceAll("[^0-9.]", ""));
                            }
                        }
                        if (topPos != null) {
                            if (position != null) {
                                //Display Position not %
                                if (position == 1) {
                                    check.setItemCount(Formatting.YELLOW + "#1");
                                }
                                else if (position == 2) {
                                    check.setItemCount(Formatting.WHITE + "#2");
                                }
                                else if (position == 3) {
                                    check.setItemCount(Formatting.GOLD + "#3");
                                }
                                else {
                                    check.setItemCount(Formatting.GRAY + "#" + position);
                                }
                            }
                            else {
                                //Display Top %
                                if (topPos <= 1) {
                                    check.setItemCount(Formatting.GREEN + String.valueOf(topPos) + Formatting.GRAY + "%");
                                }
                                else if (topPos <= 5) {
                                    check.setItemCount(Formatting.GOLD + String.valueOf(topPos) + Formatting.GRAY + "%");
                                }
                                else if (topPos <= 10) {
                                    check.setItemCount(Formatting.YELLOW + String.valueOf(topPos) + Formatting.GRAY + "%");
                                }
                                else if (topPos <= 25) {
                                    check.setItemCount(Formatting.RED + String.valueOf(topPos) + Formatting.GRAY + "%");
                                }
                                else {
                                    check.setItemCount(Formatting.DARK_RED + String.valueOf(topPos) + Formatting.GRAY + "%");
                                }
                            }
                        }
                    }
                }

                return true;
            }
        };

    }

    private static void modifyItemTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (context.isAdvanced()) {
            for (int i = lines.size() - 1; i >= 0; i--) {
                if (lines.get(i).getString().matches("NBT: \\d+ tag\\(s\\)")) {
                    lines.remove(i);
                }
            }
        }
        //This is subject to change soon this is temporary
        List<Text> texts = (((ICusomItemDataAccess) (Object) stack)).BBsentialsAll$getItemRenderTooltip();
        if (texts == null) return;
        lines.clear();
        lines.addAll(texts);
    }

    public boolean isRegistered() {
        return defsBlocking.get(renderDefId) != null || defsNonBlocking.get(renderDefId) != null;
    }

    public void removeFromPool() {
        defsBlocking.remove(renderDefId);
        defsNonBlocking.remove(renderDefId);
    }

    public Runnable getSelfRemove() {
        return this::removeFromPool;
    }

    /**
     * Try to filter out non matching items out as soon as you can before you do the intensive stuff since it takes more performance otherwise!
     *
     * @return return value defines whether you want to stop after the check
     */
    public abstract boolean modifyItem(ItemStack stack, RenderStackItemCheck check, String itemName);

    public static class RenderStackItemCheck {
        private final ItemStack stack;
        private String texturePath = null;
        private String itemCount = null;
        private List<Text> texts = null;
        private List<Text> itemLore = null;
        private Item renderAsItem = null;

        public RenderStackItemCheck(ItemStack stack) {
            this.stack = stack;
            renderAsItem = stack.getItem();
            String itemName = stack.getName().getString();
            for (RenderingDefinitions def : defsNonBlocking.values()) {
                def.modifyItem(stack, this, itemName);
            }
            for (RenderingDefinitions def : defsBlocking.values()) {
                if (def.modifyItem(stack, this, itemName)) {
                    return;
                }
            }
            getTextTooltip();
        }

        public List<Text> getTextTooltip() {
            if (texts != null) return texts;
            NbtCompound b = stack.getNbt();
            if (b == null) return new ArrayList<>();
            NbtCompound b1 = b.getCompound("display");
            if (b1 == null) return new ArrayList<>();
            NbtList list = b1.getList("Lore", NbtElement.STRING_TYPE);
            List<Text> text = new ArrayList<>();
            text.add(stack.getName());
            for (int i = 0; i < list.size(); i++) {
                text.add(Text.Serialization.fromJson(list.get(i).asString()));
            }
            texts = text;
            return text;
        }

        public List<Text> getItemLore() {
            if (itemLore != null) return itemLore;
            itemLore = new ArrayList<>();
            NbtCompound b = stack.getNbt();
            if (b == null) return itemLore;
            NbtCompound b1 = b.getCompound("display");
            if (b1 == null) return itemLore;
            NbtList list = b1.getList("Lore", NbtElement.STRING_TYPE);
            if (list == null) return itemLore;
            itemLore = new ArrayList<>(list.stream().map(t -> Text.Serialization.fromJson(t.asString())).toList());
            return itemLore;
        }

        public List<Text> getTextCopy() {
            List<Text> text = getTextTooltip();
            return new ArrayList<>(text);
        }

        public String getItemCount() {
            return itemCount;
        }

        public void setItemCount(String itemCount) {
            this.itemCount = itemCount;
        }

        public void setItemCount(int value) {
            this.itemCount = String.valueOf(value);
        }

        public String getTexturePath() {
            return texturePath;
        }

        public void setTexturePath(String texturePath) {
            this.texturePath = texturePath;
        }

        public void setTexts(List<Text> texts) {
            this.texts = texts;
        }

        public void renderAsItem(Item renderAsItem) {
            texturePath = null;
            this.renderAsItem = renderAsItem;
        }

        public Text getItemStackName() {
            return getTextTooltip().get(0);
        }

        public void setItemStackName(Text itemStackName) {
            if (texts == null) getTextTooltip();
            texts.set(0, itemStackName);
        }

        public Item getRenderAsItem() {
            return renderAsItem;
        }
    }
}