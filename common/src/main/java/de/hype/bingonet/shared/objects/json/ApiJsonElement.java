package de.hype.bingonet.shared.objects.json;

import com.google.gson.JsonElement;

public class ApiJsonElement {
    JsonElement element;
    boolean isOffPath;

    public ApiJsonElement(JsonElement element) {
        this.element = element;
        if (element == null) isOffPath = true;
    }

    public ApiJson getAsObject() {
        return ApiJson.of(element.getAsJsonObject());
    }

    public String getString() {
        return getString("");
    }

    public Long getLong() {
        return getLong(0L);
    }

    public int getInt() {
        return getInt(0);
    }

    public Boolean getBoolean() {
        return getBoolean(false);
    }

    public String getString(String def) {
        if (isOffPath) return def;
        if (element == null || element.isJsonNull()) return def;
        return element.getAsString();
    }

    public int getInt(int def) {
        if (isOffPath) return def;
        if (element == null || element.isJsonNull()) return def;
        return element.getAsInt();
    }

    public Long getLong(Long def) {
        if (isOffPath) return def;
        if (element == null || element.isJsonNull()) return def;
        return element.getAsLong();
    }

    public boolean getBoolean(boolean def) {
        if (isOffPath) return def;
        if (element == null || element.isJsonNull()) return def;
        return element.getAsBoolean();
    }
}
