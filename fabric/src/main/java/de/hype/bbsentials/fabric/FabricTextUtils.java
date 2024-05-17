package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.mclibraries.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FabricTextUtils implements TextUtils {
    @Override
    public String getContentFromJson(String json) {
        return Text.Serialization.fromJson(json, MinecraftClient.getInstance().world.getRegistryManager()).getString();
    }

    @Override
    public String getJsonFromContent(String content) {
        return Text.Serialization.toJsonString(Text.of(content), MinecraftClient.getInstance().world.getRegistryManager());
   }

   public static String textToJson(Text text){
        return Text.Serialization.toJsonString(text,MinecraftClient.getInstance().world.getRegistryManager());
   }

    public static Text jsonToText(String textJson){
        return Text.Serialization.fromJson(textJson,MinecraftClient.getInstance().world.getRegistryManager());
    }

    /**
     * The opposite method serializes or deserializes automatically dependent on the input
     */
    public static String opposite(Text text){
        return Text.Serialization.toJsonString(text,MinecraftClient.getInstance().world.getRegistryManager());
    }
    /**
     * The opposite method serializes or deserializes automatically dependent on the input
     */
    public static Text opposite(String textJson){
        return Text.Serialization.fromJson(textJson,MinecraftClient.getInstance().world.getRegistryManager());
    }

    public static String literalJson(String literal){
        return textToJson(Text.literal(literal));
    }
}
