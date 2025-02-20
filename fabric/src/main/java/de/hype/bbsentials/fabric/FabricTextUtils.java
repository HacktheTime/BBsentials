package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.mclibraries.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FabricTextUtils implements TextUtils {
    public static String textToJson(Text text) {
        if (text == null) return null;
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;
        return Text.Serialization.toJsonString(text, world.getRegistryManager());
    }

    public static Text jsonToText(String textJson) {
        if (textJson == null) return null;
        if (textJson.isEmpty()) return Text.literal("");
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;
        return Text.Serialization.fromJson(textJson, world.getRegistryManager());
    }

    /**
     * The opposite method serializes or deserializes automatically dependent on the input
     */
    public static String opposite(Text text) {
        return textToJson(text);
    }

    /**
     * The opposite method serializes or deserializes automatically dependent on the input
     */
    public static Text opposite(String textJson) {
        return jsonToText(textJson);
    }

    public static String literalJson(String literal) {
        return textToJson(Text.literal(literal));
    }

    @Override
    public de.hype.bbsentials.client.common.mclibraries.interfaces.Text createText(String content) {
        return new de.hype.bbsentials.fabric.Text(Text.literal(content));
    }

    @Override
    public String getContentFromJson(String json) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;
        return Text.Serialization.fromJson(json, world.getRegistryManager()).getString();
    }

    @Override
    public String getJsonFromContent(String content) {
        return Text.Serialization.toJsonString(Text.of(content), MinecraftClient.getInstance().world.getRegistryManager());
    }
}
