package de.hype.bbsentials.client.common.mclibraries.interfaces;

import java.util.Set;

public interface NBTCompound {
    Set<String> getKeys();

    Long getLong(String key);

    int getInt(String potionLevel);

    String get(String key);
}
