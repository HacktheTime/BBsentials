package de.hype.bbsentials.shared.objects;

import me.nullicorn.nedit.type.NBTCompound;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Item {
    NBTCompound attributes;
    NBTCompound display;
    Integer itemCount;
    Instant creationDate;
    String itemID;
    List<String> lore;

    public Item(NBTCompound object) {
        NBTCompound tags = object.getCompound("tag");
        attributes = tags.getCompound("ExtraAttributes");
        display = tags.getCompound("display");
        itemCount = object.getInt("Count", 1);
    }

    public String getItemID() {
        if (itemID == null) itemID = attributes.getString("id");
        return itemID;
    }

    public Instant getCreationDate() {
        if (creationDate == null) creationDate = Instant.ofEpochMilli(attributes.getLong("timestamp", 0));
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
        display.getList("Lore").forEachString((string) -> lore.add(string));
        return lore;
    }

    public NBTCompound getAttributes() {
        return attributes;
    }
}
