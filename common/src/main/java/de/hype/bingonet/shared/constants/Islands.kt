package de.hype.bingonet.shared.constants;

/**
 * List of all Islands
 * {@link #CRYSTAL_HOLLOWS}
 * {@link #CRIMSON_ISLE}
 * {@link #DEEP_CAVERNS}
 * {@link #DUNGEON}
 * {@link #DUNGEON_HUB}
 * {@link #DWARVEN_MINES}
 * {@link #GOLD_MINE}
 * {@link #HUB}
 * {@link #KUUDRA}
 * {@link #PRIVATE_ISLAND}
 * {@link #SPIDERS_DEN}
 * {@link #THE_END}
 * {@link #THE_FARMING_ISLANDS}
 * {@link #JERRYS_WORKSHOP}
 * {@link #THE_RIFT}
 */
public enum Islands implements BBDisplayNameProvider {
    CRYSTAL_HOLLOWS("crystal_hollows", "Crystal Hollows", "nucleus"),
    CRIMSON_ISLE("crimson_isle", "Crimson Isle", "crimson"),
    DEEP_CAVERNS("mining_2", "Deep Caverns", "deep"),
    DUNGEON("dungeon", "Dungeon"),
    DUNGEON_HUB("dungeon_hub", "Dungeon Hub", "dhub"),
    DWARVEN_MINES("mining_3", "Dwarven Mines", "mines"),
    GOLD_MINE("mining_1", "Gold Mine", "gold"),
    HUB("hub", "Hub", "hub"),
    GLACITE_TUNNEL("mineshaft", "Mineshaft"),
    KUUDRA("kuudra", "Kuudra"),
    PRIVATE_ISLAND("dynamic", "Private Island", "home"),
    SPIDERS_DEN("combat_1", "Spider's Den", "spider"),
    THE_END("combat_3", "The End", "end"),
    THE_FARMING_ISLANDS("farming_1", "The Farming Islands", "barn"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop"),
    THE_RIFT("rift", "The Rift"),
    The_Park("foraging_1", "The Park", "park"),
    Dark_Auction("dark_auction", "Dark Auction");
    private final String internalName;
    private final String displayName;

    Islands(String internalName, String displayName) {
        this.internalName = internalName;
        this.displayName = displayName;
    }

    Islands(String internalName, String displayName, String warpArgument) {
        this(internalName, displayName);
        this.warpArgument = warpArgument;
    }

    private String warpArgument;

    public String getWarpArgument() {
        return warpArgument;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
