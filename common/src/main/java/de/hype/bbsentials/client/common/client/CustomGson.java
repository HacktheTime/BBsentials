package de.hype.bbsentials.client.common.client;

import com.google.gson.*;
import de.hype.bbsentials.shared.objects.Message;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.LobbyType;
import net.hypixel.data.type.ServerType;

import java.awt.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CustomGson {
    public static Gson ownSerializer = new GsonBuilder().create();
    public static Gson create() {
        return getBase().setPrettyPrinting().create();

    }

    public static Gson createNotPrettyPrinting() {
        return getBase().create();
    }

    private static GsonBuilder getBase() {
        return new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .registerTypeAdapter(Message.class, new MessageSerializer())
                .registerTypeAdapter(Instant.class, new InstantSerializer())
                .registerTypeAdapter(ServerType.class, new ServerTypeSerializer())
                ;
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

    private static class MessageSerializer implements JsonDeserializer<Message> {
        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ownSerializer.fromJson(json, de.hype.bbsentials.client.common.chat.Message.class);
        }
    }
    private static class InstantSerializer implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Instant.ofEpochMilli(json.getAsLong());
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toEpochMilli());
        }
    }
    private static class ServerTypeSerializer implements JsonSerializer<ServerType>, JsonDeserializer<ServerType> {
        private Map<String, ServerType> types = new HashMap<>();
        public ServerTypeSerializer(){
            for (GameType value : GameType.values()) {
                types.put(value.toString(),value);
            }
            for (LobbyType value : LobbyType.values()) {
                types.put(value.toString(),value);
            }
        }
        @Override
        public ServerType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return types.get(json.getAsString());
        }

        @Override
        public JsonElement serialize(ServerType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
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
