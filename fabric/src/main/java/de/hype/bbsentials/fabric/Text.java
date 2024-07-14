package de.hype.bbsentials.fabric;

import net.minecraft.client.MinecraftClient;

public class Text extends de.hype.bbsentials.client.common.mclibraries.interfaces.Text {
    private net.minecraft.text.Text mcText;
    private String content;
    private String json;
    private CurrentObtain currentSource;

    public Text(net.minecraft.text.Text mcText) {
        this.mcText = mcText;
        currentSource = CurrentObtain.FROMMC;
    }

    @Override
    public String getString() {
        switch (currentSource) {
            case FROMMC -> {
                if (content == null) content = mcText.getString();
                return content;
            }
            case STRING -> {
                return content;
            }
            case JSON -> {
                return json;
            }
        }
        return null;
    }
    public void setText(net.minecraft.text.Text toValue) {
        this.mcText = toValue;
        content = null;
        json = null;
        currentSource = CurrentObtain.FROMMC;
    }
    @Override
    public void setStringText(String toValue) {
        this.content = toValue;
        mcText = null;
        json = null;
        currentSource = CurrentObtain.STRING;
    }
    @Override
    public void setJsonText(String toValue) {
        this.json = toValue;
        mcText = null;
        content = null;
        currentSource = CurrentObtain.JSON;
    }
    @Override
    public String toJson() {
        if (json != null) return json;
        if (mcText == null) mcText = net.minecraft.text.Text.literal(content);
        return MinecraftClient.getInstance().world != null ? net.minecraft.text.Text.Serialization.toJsonString(mcText, MinecraftClient.getInstance().world.getRegistryManager()) : null;
    }

    public de.hype.bbsentials.client.common.mclibraries.interfaces.Text createNew(String content){
        return new Text(net.minecraft.text.Text.literal(""));
    }

    @Override
    public String toString() {
        return getString();
    }
    public net.minecraft.text.Text getAsText() {
        switch (currentSource) {
            case FROMMC -> {
                return mcText;
            }
            case STRING -> {
                return net.minecraft.text.Text.literal(content);
            }
            case JSON -> {
                return MinecraftClient.getInstance().world != null ? net.minecraft.text.Text.Serialization.fromJson(json, MinecraftClient.getInstance().world.getRegistryManager()) : null;
            }
        }
        return null;
    }


    private static enum CurrentObtain {
        FROMMC,
        STRING,
        JSON
    }
}
