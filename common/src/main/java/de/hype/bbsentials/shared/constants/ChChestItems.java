package de.hype.bbsentials.shared.constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration representing various ChChest items in the game.
 * These constants define specific ChChest items that players can obtain.
 * Use these constants to refer to specific ChChest items. For non-listed use the Custom Value. Example usage below.
 * <p>
 * The available ChChest items are:
 * <ul>
 *     <li>{@code PrehistoricEgg}: Represents a prehistoric egg item.</li>
 *     <li>{@code Pickonimbus2000}: Represents the Pickonimbus 2000 item.</li>
 *     <li>{@code ControlSwitch}: Represents a control switch item.</li>
 *     <li>{@code ElectronTransmitter}: Represents an electron transmitter item.</li>
 *     <li>{@code FTX3070}: Represents the FTX 3070 item.</li>
 *     <li>{@code RobotronReflector}: Represents the Robotron Reflector item.</li>
 *     <li>{@code SuperliteMotor}: Represents a Superlite Motor item.</li>
 *     <li>{@code SyntheticHeart}: Represents a synthetic heart item.</li>
 *     <li>{@code FlawlessGemstone}: Represents a flawless gemstone item.</li>
 *     <li>{@code JungleHeart}: Represents a Jungle Heart item.</li>
 * </ul>
 * How to create a Custom Enum:
 * <pre>
 * {@code new ChChestItem("(Your Item name)")}
 * Make sure too use the EXACT display name!
 * </pre>
 */
public class ChChestItems {
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
    private static final List<ChChestItem> items = new ArrayList<>();

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