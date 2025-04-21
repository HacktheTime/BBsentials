package de.hype.bingonet.shared.objects;

import com.google.gson.JsonObject;
import de.hype.bingonet.shared.objects.json.ApiJson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Item {
    ApiJson attributes;
    ApiJson display;
    Integer itemCount;
    Instant creationDate;
    String itemID;
    Integer mcItemId;
    Integer damage;
    List<String> lore;

    public Item(JsonObject object) {
        this(new ApiJson(object));
    }

    public Item(ApiJson data) {
        attributes = data.get("tag→ExtraAttributes").copyApplied();
        display = data.get("tag→display");
        mcItemId = data.getInt("id");
        damage = data.getInt("Damage", 0);
        itemCount = data.getInt("Count", 1);
    }

    public String getItemID() {
        if (itemID == null) itemID = attributes.getString("id");
        return itemID;
    }

    public Instant getCreationDate() {
        if (creationDate == null) creationDate = Instant.ofEpochMilli(attributes.getLong("timestamp", 0L));
        return creationDate;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public String getDisplayName() {
        return display.getString("Name");
    }

    public List<String> getLore() {
        if (lore != null) return lore;
        lore = new ArrayList<>();
        display.getJsonArray("Lore").toList().forEach(((e) -> lore.add(e.getString())));
        return lore;
    }

    public ApiJson getAttributes() {
        return attributes;
    }

    public enum AttributeType {
        ENCHANTMETS(1),
        ATTRIBUTE_MODIFIERS(2),
        UNBREAKABLE(3),
        CAN_DESTROY(4),
        CAN_PLACE_ON(5),
        VARIOUS(6),
        DYED(7),
        UPGRADES(8);


        public final Integer bitId;

        AttributeType(Integer bitId) {
            this.bitId = bitId;
        }
    }
}
