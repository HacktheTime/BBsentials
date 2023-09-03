package de.hype.bbsentials.constants.enviromentShared;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChChestItems {
    private static final List<ChChestItem> items = new ArrayList<>();

    public static final ChChestItem PrehistoricEgg = new ChChestItem("Prehistoric Egg");
    public static final ChChestItem Pickonimbus2000 = new ChChestItem("Pickonimbus 2000");
    public static final ChChestItem ControlSwitch = new ChChestItem("Control Switch");
    public static final ChChestItem ElectronTransmitter = new ChChestItem("Electron Transmitter");
    public static final ChChestItem FTX3070 = new ChChestItem("FTX 3070");
    public static final ChChestItem RobotronReflector = new ChChestItem("Robotron Reflector");
    public static final ChChestItem SuperliteMotor = new ChChestItem("Superlite Motor");
    public static final ChChestItem SyntheticHeart = new ChChestItem("Synthetic Heart");
    public static final ChChestItem FlawlessGemstone = new ChChestItem("Flawless Gemstone");
    public static final ChChestItem JungleHeart = new ChChestItem("Jungle Heart");

    // Automatically populate predefined items using reflection
    static {
        Field[] fields = ChChestItems.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(ChChestItem.class) && isPublicStaticFinal(field)) {
                try {
                    items.add((ChChestItem) field.get(null));
                } catch (IllegalAccessException e) {
                    // Handle exception
                }
            }
        }
    }

    public static ChChestItem getItem(String displayName) {
        ChChestItem existingItem = getPredefinedItem(displayName);

        if (existingItem != null) {
            return existingItem;
        }

        ChChestItem customItem = new ChChestItem(displayName, true);
        return customItem;
    }

    private static ChChestItem getPredefinedItem(String displayName) {
        for (ChChestItem item : items) {
            if (item.getDisplayName().equals(displayName)) {
                return item;
            }
        }
        return null;
    }

    public static ChChestItem[] getItem(String[] displayNames) {
        ChChestItem[] result = new ChChestItem[displayNames.length];
        for (int i = 0; i < displayNames.length; i++) {
            result[i] = getItem(displayNames[i]);
        }
        return result;
    }

    // Utility method to check if a field is public, static, and final
    private static boolean isPublicStaticFinal(Field field) {
        return java.lang.reflect.Modifier.isPublic(field.getModifiers()) &&
                java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                java.lang.reflect.Modifier.isFinal(field.getModifiers());
    }

    public static ChChestItem createCustomItem(String displayName) {
        ChChestItem customItem = new ChChestItem(displayName, true);
        items.add(customItem);
        return customItem;
    }

    public static List<ChChestItem> getAllItems() {
        return items;
    }

    public static List<String> getAllItemNames() {
        return items.stream()
                .map(ChChestItem::getDisplayName)
                .collect(Collectors.toList());
        //very fancy way to convert a list to a list of values from the previous list
    }
}