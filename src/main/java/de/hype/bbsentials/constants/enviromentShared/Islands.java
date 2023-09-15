package de.hype.bbsentials.constants.enviromentShared;

import de.hype.bbsentials.constants.BBDisplayNameProvider;

public enum Islands implements BBDisplayNameProvider {
    CRYSTAL_HOLLOWS("crystal_hollows", "Crystal Hollows"),
    CRIMSON_ISLE("crimson_isle", "Crimson Isle"),
    DEEP_CAVERNS("mining_2", "Deep Caverns"),
    DUNGEON("dungeon", "Dungeon"),
    DUNGEON_HUB("dungeon_hub", "Dungeon Hub"),
    DWARVEN_MINES("mining_3", "Dwarven Mines"),
    GOLD_MINE("mining_1", "Gold Mine"),
    HUB("hub", "Hub"),
    KUUDRA("kuudra", "Kuudra"),
    PRIVATE_ISLAND("dynamic", "Private Islands"),
    SPIDERS_DEN("combat_1", "Spider's Den"),
    THE_END("combat_3", "The End"),
    THE_FARMING_ISLANDS("farming_1", "The Farming Islands"),
    THE_RIFT("rift", "The Rift");


    private final String internalName;
    private final String displayName;

    Islands(String internalName, String displayName) {
        this.internalName = internalName;
        this.displayName = displayName;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
