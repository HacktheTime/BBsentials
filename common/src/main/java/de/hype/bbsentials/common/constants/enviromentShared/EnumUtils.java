package de.hype.bbsentials.common.constants.enviromentShared;

import de.hype.bbsentials.common.constants.BBDisplayNameProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumUtils {
    public static List<String> getAllDisplayNames(Class<? extends BBDisplayNameProvider> enumClass) {
        List<String> displayNames = new ArrayList<>();

        for (BBDisplayNameProvider item : enumClass.getEnumConstants()) {
            displayNames.add(item.getDisplayName());
        }

        return displayNames;
    }

    public static <T extends Enum<T> & BBDisplayNameProvider> List<T> getEnumsAsList(Class<T> enumClass) {
        List<T> enumList = new ArrayList<>();

        for (T item : enumClass.getEnumConstants()) {
            enumList.add(item);
        }

        return enumList;
    }

    public static List<String> getDisplayNames(Collection<? extends BBDisplayNameProvider> itemList) {
        List<String> displayNames = new ArrayList<>();
        for (BBDisplayNameProvider item : itemList) {
            displayNames.add(item.getDisplayName());
        }
        return displayNames;
    }

    public static <T extends BBDisplayNameProvider> List<T> getEnumsAsList(List<T> itemList) {
        List<T> enumList = new ArrayList<>(itemList);
        return enumList;
    }

    public static List<String> getAllEnumNames(Class<? extends Enum<?>> enumClass) {
        List<String> enumNames = new ArrayList<>();
        Enum<?>[] enumConstants = enumClass.getEnumConstants();

        for (Enum<?> enumConstant : enumConstants) {
            enumNames.add(enumConstant.name());
        }

        return enumNames;
    }

//    public interface BBDisplayNameProvider {
//        String getDisplayName();
//        default public String serialize() {
//            return name() + ":" + displayName;
//        }
//
//        default public ChChestItems deserialize(String serializedValue) {
//            String[] parts = serializedValue.split(":");
//            if (parts.length != 2) {
//                throw new IllegalArgumentException("Invalid serialized value format");
//            }
//            String enumName = parts[0];
//            String displayName = parts[1];
//
//            return ChChestItems.valueOf(enumName).setDisplayName(displayName);
//        }
//    }

    public static <T extends Enum<T> & BBDisplayNameProvider> T getEnumByName(Class<T> enumClass, String enumName) {
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (IllegalArgumentException e) {
            return null; // Enum value not found
        }
    }

    public static <T extends Enum<T> & BBDisplayNameProvider> T getEnumByValue(Class<T> enumClass, String value) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getDisplayName().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    public static <T extends Enum<T> & BBDisplayNameProvider> T[] getEnumsByName(Class<T> enumClass, String[] names) {
        List<T> matchingEnums = new ArrayList<>();

        for (String name : names) {
            boolean found = false;
            for (T enumValue : enumClass.getEnumConstants()) {
                if (enumValue.name().equals(name)) {
                    matchingEnums.add(enumValue);
                    found = true;
                    break;
                }
            }
        }

        return matchingEnums.toArray((T[]) java.lang.reflect.Array.newInstance(enumClass, 0));
    }

    public static <T extends Enum<T> & BBDisplayNameProvider> T[] getEnumsByValue(Class<T> enumClass, String[] values) {
        List<T> matchingEnums = new ArrayList<>();

        for (String value : values) {
            boolean found = false;
            for (T enumValue : enumClass.getEnumConstants()) {
                if (enumValue.getDisplayName().equals(value)) {
                    matchingEnums.add(enumValue);
                    found = true;
                    break;
                }
            }
        }

        return matchingEnums.toArray((T[]) java.lang.reflect.Array.newInstance(enumClass, 0));
    }
}


