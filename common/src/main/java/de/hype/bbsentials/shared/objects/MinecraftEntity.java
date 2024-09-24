package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.VanillaEntities;

public class MinecraftEntity {
    public final VanillaEntities type;
    public final String customName;
    public final Position position;

    public MinecraftEntity(VanillaEntities type, String customName, Position position) {
        this.type = type;
        this.customName = customName;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public String getCustomName() {
        return customName;
    }

    public VanillaEntities getType() {
        return type;
    }
}