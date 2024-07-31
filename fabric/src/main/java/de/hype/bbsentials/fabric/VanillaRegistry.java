package de.hype.bbsentials.fabric;

import de.hype.bbsentials.shared.constants.VanillaBlocks;
import de.hype.bbsentials.shared.constants.VanillaEntities;
import de.hype.bbsentials.shared.constants.VanillaItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class VanillaRegistry {
    public static Map<VanillaItems, Item> itemsMap = new HashMap<>();
    public static Map<Item, VanillaItems> mcitemsMap = new HashMap<>();
    public static Map<VanillaEntities, String> entitysMap = new HashMap<>();
    public static Map<String, VanillaEntities> mcentityMap = new HashMap<>();

    public static Map<VanillaBlocks, String> blockMap = new HashMap<>();
    public static Map<String, VanillaBlocks> mcBlockMap = new HashMap<>();

    public static Item get(VanillaItems item) {
        if (itemsMap.isEmpty()) init();
        return itemsMap.get(item);
    }

    public static VanillaItems get(net.minecraft.item.Item item) {
        if (mcitemsMap.isEmpty()) init();
        return mcitemsMap.get(item);
    }

    public static String get(VanillaEntities entity) {
        if (entitysMap.isEmpty()) init();
        return entitysMap.get(entity);
    }

    public static VanillaEntities get(Entity entity) {
        if (mcentityMap.isEmpty()) init();
        return mcentityMap.get(entity.getType().getUntranslatedName());
    }

    public static String get(VanillaBlocks entity) {
        if (blockMap.isEmpty()) init();
        return blockMap.get(entity);
    }

    public static VanillaBlocks get(Block block) {
        if (mcBlockMap.isEmpty()) init();
        return mcBlockMap.get(Registries.BLOCK.getId(block).getPath());
    }
    public static void init() {
        List<String> missingEnums = new ArrayList<>();
        for (RegistryEntry<Item> indexedEntry : Registries.ITEM.getIndexedEntries()) {
            Optional<RegistryKey<Item>> registry = indexedEntry.getKey();
            if (registry.isEmpty()) continue;
            Identifier identifier = registry.get().getValue();
            if (identifier.getNamespace().equals("minecraft")) {
                try {
                    VanillaItems vanilla = VanillaItems.valueOf(identifier.getPath().toUpperCase());
                    itemsMap.put(vanilla, indexedEntry.value());
                    mcitemsMap.put(indexedEntry.value(), vanilla);
                } catch (Exception e) {
                    missingEnums.add("Item: " + identifier.getPath());
                }
            }

        }

        for (RegistryEntry<EntityType<?>> indexedEntry : Registries.ENTITY_TYPE.getIndexedEntries()) {
            Optional<RegistryKey<EntityType<?>>> registry = indexedEntry.getKey();
            if (registry.isEmpty()) continue;
            Identifier identifier = registry.get().getValue();
            if (identifier.getNamespace().equals("minecraft")) {
                try {
                    VanillaEntities vanilla = VanillaEntities.valueOf(identifier.getPath().toUpperCase());
                    String baseId = indexedEntry.value().getUntranslatedName();
                    entitysMap.put(vanilla, baseId);
                    mcentityMap.put(baseId, vanilla);
                } catch (Exception e) {
                    missingEnums.add("Entity: " + identifier.getPath());
                }
            }
        }
        for (RegistryEntry<Block> indexedEntry : Registries.BLOCK.getIndexedEntries()) {
            Optional<RegistryKey<Block>> registry = indexedEntry.getKey();
            if (registry.isEmpty()) continue;
            Identifier identifier = registry.get().getValue();
            if (identifier.getNamespace().equals("minecraft")) {
                try {
                    VanillaBlocks vanilla = VanillaBlocks.valueOf(identifier.getPath().toUpperCase());
                    String baseId = Registries.BLOCK.getId(indexedEntry.value()).getPath();
                    blockMap.put(vanilla, baseId);
                    mcBlockMap.put(baseId, vanilla);
                } catch (Exception e) {
                    missingEnums.add("Block: " + identifier.getPath());
                }
            }
        }

        if (!missingEnums.isEmpty()) {
            System.err.println("Missing the following new Vanilla Enums: " + missingEnums);
        }
    }

}
