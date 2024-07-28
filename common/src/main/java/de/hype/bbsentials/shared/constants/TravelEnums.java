package de.hype.bbsentials.shared.constants;

public enum TravelEnums {
    private_island("home", Islands.PRIVATE_ISLAND,true),
    hub("hub",Islands.HUB,true),
    village("village",Islands.HUB,true),
    elizabeth("elizabeth",Islands.HUB,false),
    castle("castle",Islands.HUB,false),
    da("da",Islands.HUB,false),
    crypt("crypt",Islands.HUB,false),
    crypts("crypts",Islands.HUB,false),
    museum("museum",Islands.HUB,false),
    dungeon_hub("dungeon_hub",Islands.DUNGEON_HUB,true),
    dungeons("dungeons",Islands.DUNGEON_HUB,true),
    dhub("dhub",Islands.DUNGEON_HUB,true),
    barn("barn",Islands.THE_FARMING_ISLANDS,true),
    desert("desert",Islands.THE_FARMING_ISLANDS,false),
    trapper("trapper",Islands.THE_FARMING_ISLANDS,false),
    trap("trap",Islands.THE_FARMING_ISLANDS,false),
    park("park",Islands.THE_PARK,true),
    jungle("jungle",Islands.THE_PARK,false),
    howl("howl",Islands.THE_PARK,false),
    gold("gold",Islands.GOLD_MINE,true),
    deep("deep",Islands.DEEP_CAVERNS,true),
    mines("mines",Islands.DWARVEN_MINES,true),
    forge("forge",Islands.DWARVEN_MINES,false),
    crystals("crystals",Islands.CRYSTAL_HOLLOWS,true),
    hollows("hollows",Islands.CRYSTAL_HOLLOWS,true),
    nucleus("nucleus",Islands.CRYSTAL_HOLLOWS,false),
    spider("spider",Islands.SPIDERS_DEN,true),
    spiders("spiders",Islands.SPIDERS_DEN,true),
    top("top",Islands.SPIDERS_DEN,false),
    nest("nest",Islands.SPIDERS_DEN,false),
    mound("mound",Islands.SPIDERS_DEN,false),
    arachne("arachne",Islands.SPIDERS_DEN,false),
    end("end",Islands.THE_END,true),
    drag("drag",Islands.THE_END,false),
    VOID("void",Islands.THE_END,false),
    sepulture("sepulture",Islands.THE_END,false),
    crimson("crimson",Islands.CRIMSON_ISLE,true),
    nether("nether",Islands.CRIMSON_ISLE,true),
    isle("isle",Islands.CRIMSON_ISLE,true),
    kuudra("kuudra",Islands.CRIMSON_ISLE,false),
    wasteland("wasteland",Islands.CRIMSON_ISLE,false),
    dragontail("dragontail",Islands.CRIMSON_ISLE,false),
    scarleton("scarleton",Islands.CRIMSON_ISLE,false),
    smoldering("smoldering",Islands.CRIMSON_ISLE,false),
    smoldering_tomb("smoldering_tomb",Islands.CRIMSON_ISLE,false),
    smold("smold",Islands.CRIMSON_ISLE,false),
    garden("garden",Islands.GARDEN,true),
    winter("winter",Islands.JERRYS_WORKSHOP,true),
    jerry("jerry",Islands.JERRYS_WORKSHOP,true),
    workshop("workshop",Islands.JERRYS_WORKSHOP,true),
    basecamp("basecamp",Islands.DWARVEN_MINES,false),
    camp("camp",Islands.DWARVEN_MINES,false),
    glacite("glacite",Islands.DWARVEN_MINES,false),
    base("base",Islands.DWARVEN_MINES,false),
    tunnels("tunnels",Islands.DWARVEN_MINES,false),
    tunnel("tunnel",Islands.DWARVEN_MINES,false),
    gt("gt",Islands.DWARVEN_MINES,false),
    dungeon("floordungeon",Islands.DUNGEON,true)

    ;
    String travelArgument;
    Islands island;
    boolean isDefaultSpawn;
    TravelEnums(String travelArgument, Islands island, boolean isDefaultSpawn) {
        this.island=island;
        this.travelArgument=travelArgument;
        this.isDefaultSpawn=isDefaultSpawn;
    }

    public String getTravelArgument() {
        return travelArgument;
    }

    public Islands getIsland() {
        return island;
    }

    public boolean isDefaultSpawn() {
        return isDefaultSpawn;
    }
}
