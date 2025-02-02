package de.hype.bbsentials.shared.objects.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ApiJsonList {
    ApiJson parent;
    List<ApiJsonElement> values = new ArrayList<>();

    public ApiJsonList(ApiJson parent, String key) {
        this.parent = parent;
        JsonElement temp = parent.getJSONElementSafe();
        if (temp == null) return;
        JsonArray array = temp.getAsJsonObject().getAsJsonArray(key);
        if (array == null) return;
        for (JsonElement jsonElement : array) {
            values.add(new ApiJsonElement(jsonElement));
        }
    }

    public List<ApiJsonElement> toList() {
        return new ArrayList<>(values);
    }
}
