package de.hype.bbsentials.constants;

public interface BBDisplayNameProvider {
    String getDisplayName();

    default String serialize() {
        return name() + ":" + getDisplayName();
    }

    default String name() {
        return ((Enum<?>) this).name();
    }

//    public static BBDisplayNameProvider deserialize(String serializedValue) {
//        String[] parts = serializedValue.split(":");
//        if (parts.length != 2) {
//            throw new IllegalArgumentException("Invalid serialized value format");
//        }
//        String enumName = parts[0];
//        String displayName = parts[1];
//        return ;
//    }
}
