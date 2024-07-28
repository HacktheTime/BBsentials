package de.hype.bbsentials.fabric;

import de.hype.bbsentials.shared.constants.VanillaItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    public static Map<VanillaItems, Item> itemsMap = new HashMap<>();
    public static Map<Item, VanillaItems> mcitemsMap = new HashMap<>();

    public static Item getItem(VanillaItems item){
        if (itemsMap.isEmpty()) init();
        return itemsMap.get(item);
    }
    public static VanillaItems getItem(net.minecraft.item.Item item){
        if (mcitemsMap.isEmpty()) init();
        return mcitemsMap.get(item);
    }

    public static void init(){
        List<String> missingEnums = new ArrayList<>();
        for (Field declaredField : Items.class.getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                Item value = (Item) declaredField.get(null);
                try {
                    VanillaItems vanilla = VanillaItems.valueOf(((Item) declaredField.get(null)).toString().split(":")[1].toUpperCase());
                    itemsMap.put(vanilla,value);
                    mcitemsMap.put(value,vanilla);
                }catch (Exception e){
                    missingEnums.add(declaredField.getName());
                }
            } catch (IllegalAccessException e) {
            }
        }
        if (!missingEnums.isEmpty()) {
            System.err.println("Missing the following new Vanilla Enums: "+missingEnums);
        }
    }
}
