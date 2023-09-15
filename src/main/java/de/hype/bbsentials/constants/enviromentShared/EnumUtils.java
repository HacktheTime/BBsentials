package de.hype.bbsentials.constants.enviromentShared;

import de.hype.bbsentials.constants.BBDisplayNameProvider;
import de.hype.bbsentials.constants.BBDisplayNameProviderWithCustom;

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



    // Methods for BBDisplayNameProviderWithCustom

    public static <T extends Enum<T> & BBDisplayNameProviderWithCustom> T getEnumByNameWithCustom(Class<T> enumClass, String enumName) {
        boolean found = false;
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(enumName)) {
                return (enumValue);
            }
        }
        return createCustomEnum(enumClass, enumName);
    }

    public static <T extends Enum<T> & BBDisplayNameProviderWithCustom> T getEnumByValueWithCustom(Class<T> enumClass, String value) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getDisplayName().equals(value)) {
                return enumValue;
            }
        }
        return (createCustomEnum(enumClass, value));

    }

    public static <T extends Enum<T> & BBDisplayNameProviderWithCustom> T[] getEnumsByNameWithCustom(Class<T> enumClass, String[] names) {
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
            if (!found) {
                matchingEnums.add(createCustomEnum(enumClass, name));
            }
        }

        return matchingEnums.toArray((T[]) java.lang.reflect.Array.newInstance(enumClass, 0));
    }

    public static <T extends Enum<T> & BBDisplayNameProviderWithCustom> T[] getEnumsByValueWithCustom(Class<T> enumClass, String[] values) {
        List<T> matchingEnums = new ArrayList<>();

        for (String value : values) {
            boolean found = false;
            for (T enumValue : enumClass.getEnumConstants()) {
                if (enumValue.toString().equals(value)) {
                    matchingEnums.add(enumValue);
                    found = true;
                    break;
                }
            }
            if (!found) {
                matchingEnums.add(createCustomEnum(enumClass, value));
            }
        }

        return matchingEnums.toArray((T[]) java.lang.reflect.Array.newInstance(enumClass, 0));
    }

    // Methods for BBDisplayNameProvider

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

    private static <T extends Enum<T> & BBDisplayNameProviderWithCustom> T createCustomEnum(Class<T> enumClass, String value) {
        T customEnum = null;
        try {
            customEnum = Enum.valueOf(enumClass, "Custom");
        } catch (Exception ignored) {
            try {
                customEnum = Enum.valueOf(enumClass, "CUSTOM");
            } catch (Exception ignored2) {
            }
        }
        if (customEnum == null) {
        }
        customEnum.setDisplayName(value);
        return customEnum;

    }
}


