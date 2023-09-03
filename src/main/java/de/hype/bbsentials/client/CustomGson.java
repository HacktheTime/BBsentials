package de.hype.bbsentials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomGson {
    public static Gson create() {
        return new GsonBuilder()
//                .registerTypeHierarchyAdapter(BBDisplayNameProvider.class, new BBDisplayNameProviderSerializer())
                .create();

    }

//    private static class BBDisplayNameProviderSerializer implements JsonSerializer<BBDisplayNameProvider>, JsonDeserializer<BBDisplayNameProvider> {
//        @Override
//        public JsonElement serialize(BBDisplayNameProvider src, Type typeOfSrc, JsonSerializationContext context) {
//            return new JsonPrimitive(src.serialize()); // Serialize using the provided method
//        }
//
//        @Override
//        public BBDisplayNameProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            String serializedValue = json.getAsString();
//
//            // Deserialize using the provided method (you need to implement this)
//            return BBDisplayNameProvider.deserialize(serializedValue);
//        }
//    }
}
