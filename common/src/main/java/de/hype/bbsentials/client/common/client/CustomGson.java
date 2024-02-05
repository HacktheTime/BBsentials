package de.hype.bbsentials.client.common.client;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class CustomGson {
    public static Gson create() {
        return new GsonBuilder()
//                .registerTypeHierarchyAdapter(BBDisplayNameProvider.class, new BBDisplayNameProviderSerializer())
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .setPrettyPrinting()
                .create();

    }
    public static Gson createNotPrettyPrinting() {
        return new GsonBuilder()
//                .registerTypeHierarchyAdapter(BBDisplayNameProvider.class, new BBDisplayNameProviderSerializer())
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .create();

    }

    private static class ColorSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int red = jsonObject.getAsJsonPrimitive("r").getAsInt();
            int green = jsonObject.getAsJsonPrimitive("g").getAsInt();
            int blue = jsonObject.getAsJsonPrimitive("b").getAsInt();
            int alpha = jsonObject.getAsJsonPrimitive("a").getAsInt();

            return new Color(red, green, blue, alpha);
        }

        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("r", src.getRed());
            jsonObject.addProperty("g", src.getGreen());
            jsonObject.addProperty("b", src.getBlue());
            jsonObject.addProperty("a", src.getAlpha());

            return jsonObject;
        }
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
