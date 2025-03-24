package de.hype.bingonet.shared.objects.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class ApiJson {
    private boolean isOffPath = false;
    private JsonObject data;
    private List<String> path = new ArrayList<>();

    public ApiJson(JsonObject obj) {
        this.data = obj;
        if (data == null) isOffPath = true;
    }

    public ApiJson(JsonObject obj, Boolean isOffPath, List<String> path) {
        this.data = obj;
        this.isOffPath = isOffPath;
        this.path = new ArrayList<>(path);
    }

    public static ApiJson of(JsonObject obj) {
        return new ApiJson(obj);
    }

    public JsonObject getObject() {
        JsonObject temp = data;
        for (String pathPart : path) {
            temp = temp.getAsJsonObject(pathPart);
        }
        return temp;
    }

    private JsonElement getJSONElementSafe(List<String> path) {
        JsonElement temp = data;
        isOffPath = false;
        if (temp == null) return null;
        for (String pathPart : path) {
            if (temp.isJsonObject()) {
                temp = temp.getAsJsonObject().get(pathPart);
            }
            else temp = null;
            if (temp != null) continue;
            isOffPath = true;
            return null;
        }
        return temp;
    }

    public JsonObject getJSONObjectSafe() {
        return getJSONObjectSafe(path);
    }

    public JsonElement getJSONElementSafe() {
        return getJSONElementSafe(path);
    }

    private JsonObject getJSONObjectSafe(List<String> path) {
        JsonElement element = getJSONElementSafe(path);
        if (element == null) return null;
        return element.getAsJsonObject();
    }


    /**
     * @param key supports "→" for multi path. For exmaple trapper.palts is like get(trapper).get(pelts)
     * @return
     */
    public ApiJson get(String key) {
        ApiJson copy = this.copy();
        copy.path.addAll(Arrays.asList(key.split("→")));
        return copy;
    }

    private ApiJson copy() {
        return new ApiJson(data, isOffPath, path);
    }

    public ApiJsonList getJsonArray(String key) {
        return new ApiJsonList(this, key);
    }

    /**
     * Only use at the entire end since these are the default and not caught!
     *
     * @return a List of Elements.
     */
    public Set<Map.Entry<String, JsonElement>> getFinalElements() {
        JsonObject obj = getJSONObjectSafe();
        if (obj == null) return new HashSet<>();
        return obj.entrySet();
    }

    public ApiJsonElement getElement(String id) {
        try {
            String[] pathSplit = id.split("→");
            List<String> path = new ArrayList<>(this.path);
            path.addAll(Arrays.asList(pathSplit).subList(0, pathSplit.length - 1));
            JsonObject safeObj = getJSONObjectSafe(path);
            if (safeObj == null) return new ApiJsonElement(null);
            return new ApiJsonElement(safeObj.get(pathSplit[pathSplit.length - 1]));
        } catch (NullPointerException ignored) {
            return new ApiJsonElement(null);
        }
    }

    public String getString(String key) {

        return getString(key, null);
    }

    public Long getLong(String key) {
        return getLong(key, 0L);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public boolean getBoolean(String key, boolean def) {
        ApiJsonElement element = getElement(key);
        if (element == null) return def;
        return element.getBoolean(def);
    }


    public String getString(String key, String def) {
        ApiJsonElement element = getElement(key);
        if (element == null) return def;
        return element.getString(def);
    }

    public int getInt(String key, int def) {
        ApiJsonElement element = getElement(key);
        if (element == null) return def;
        return element.getInt(def);
    }

    public Long getLong(String key, Long def) {
        ApiJsonElement element = getElement(key);
        if (element == null) return def;
        return element.getLong(def);
    }

    public Map<String, ApiJson> getAllSubObjects() {
        try {
            JsonObject obj = getJSONObjectSafe();
            List<String> keys = obj.keySet().stream().toList();
            Map<String, ApiJson> subs = new HashMap<>();
            for (String key : keys) {
                subs.put(key, new ApiJson(obj.getAsJsonObject(key)));
            }
            return subs;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public boolean isOffPath() {
        return isOffPath;
    }

    public ApiJson copyApplied() {
        if (isOffPath) return new ApiJson(null);
        JsonElement temp = data;
        for (String pathPart : path) {
            if (temp.isJsonObject()) {
                temp = temp.getAsJsonObject().get(pathPart);
            }
            else temp = null;
            if (temp != null) continue;
            break;
        }
        if (isOffPath) return new ApiJson(null);
        return new ApiJson(temp.getAsJsonObject());
    }
}

