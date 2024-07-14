package de.hype.bbsentials.fabric;

import net.minecraft.nbt.NbtCompound;

import java.util.Set;

public class NBTCompound implements de.hype.bbsentials.client.common.mclibraries.interfaces.NBTCompound {
    private final NbtCompound mcCompound;
    public NBTCompound(NbtCompound mcCompound){
        this.mcCompound = mcCompound;
    }

    public NbtCompound getMcCompound() {
        return mcCompound;
    }

    @Override
    public Set<String> getKeys() {
        return mcCompound.getKeys();
    }

    @Override
    public Long getLong(String key) {
        return mcCompound.getLong(key);
    }

    @Override
    public int getInt(String key) {
        return mcCompound.getInt(key);
    }

    @Override
    public String get(String key) {
        return mcCompound.get(key).asString();
    }
}
