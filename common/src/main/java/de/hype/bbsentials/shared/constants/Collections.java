

package de.hype.bbsentials.shared.constants;

public enum Collections {
    // Farming
    Cocoa_Beans("INK_SACK:3", 75, 200, 500, 2000, 5000, 10000, 20000, 50000, 100000),
    Carrot("CARROT_ITEM", 100, 250, 500, 1750, 5000, 10000, 25000, 50000, 100000),
    Cactus("CACTUS", 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000),
    Raw_Chicken("RAW_CHICKEN", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Sugar_Cane("SUGAR_CANE", 100, 250, 500, 1000, 2000, 5000, 10000, 20000, 50000),
    Pumpkin("PUMPKIN", 40, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000, 250000),
    Wheat("WHEAT", 50, 100, 250, 500, 1000, 2500, 10000, 15000, 25000, 50000, 100000),
    Seeds("SEEDS", 50, 100, 250, 1000, 2500, 5000, 25000),
    Mushroom("MUSHROOM_COLLECTION", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Raw_Rabbit("RABBIT", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Nether_Wart("NETHER_STALK", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 75000, 100000, 250000),
    Mutton("MUTTON", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Melon("MELON", 250, 500, 1250, 5000, 15000, 25000, 50000, 100000, 250000),
    Potato("POTATO_ITEM", 100, 200, 500, 1750, 5000, 10000, 25000, 50000, 100000),
    Leather("LEATHER", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Raw_Porkchop("PORK", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Feather("FEATHER", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),

    // Mining
    Lapis_Lazuli("INK_SACK:4", 250, 500, 1000, 2000, 10000, 25000, 50000, 100000, 150000, 250000),
    Redstone("REDSTONE", 100, 250, 750, 1500, 3000, 5000, 10000, 25000, 50000, 200000, 400000, 600000, 800000, 1000000, 1200000, 1400000),
    Umber("UMBER", 1000, 2500, 10000, 25000, 100000, 250000, 500000, 750000, 1000000),
    Coal("COAL", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Mycelium("MYCEL", 50, 500, 750, 1000, 2500, 10000, 15000, 25000, 50000, 100000),
    End_Stone("ENDER_STONE", 50, 100, 250, 1000, 2500, 5000, 10000, 15000, 25000, 50000),
    Nether_Quartz("QUARTZ", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Sand("SAND", 50, 100, 250, 500, 1000, 2500, 5000),
    Iron_Ingot("IRON_INGOT", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000, 200000, 400000),
    Gemstone("GEMSTONE_COLLECTION", 100, 250, 1000, 2500, 5000, 25000, 100000, 250000, 500000, 1000000, 2000000),
    Tungsten("TUNGSTEN", 1000, 2500, 10000, 25000, 100000, 250000, 500000, 750000, 1000000),
    Obsidian("OBSIDIAN", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Diamond("DIAMOND", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Cobblestone("COBBLESTONE", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 40000, 70000),
    Glowstone_Dust("GLOWSTONE_DUST", 50, 100, 1000, 2500, 5000, 10000, 25000),
    Gold_Ingot("GOLD_INGOT", 50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000),
    Gravel("GRAVEL", 50, 100, 250, 1000, 2500, 5000, 10000, 15000, 50000),
    Hard_Stone("HARD_STONE", 50, 1000, 5000, 50000, 150000, 300000, 1000000),
    Mithril("MITHRIL_ORE", 50, 250, 1000, 2500, 5000, 10000, 250000, 500000, 1000000),
    Emerald("EMERALD", 50, 100, 250, 1000, 5000, 15000, 30000, 50000, 100000),
    Red_Sand("SAND:1", 50, 500, 2500, 10000, 15000, 25000, 50000, 100000),
    Ice("ICE", 50, 100, 250, 500, 1000, 5000, 10000, 50000, 100000, 250000, 500000),
    Glacite("GLACITE", 1000, 2500, 10000, 25000, 100000, 250000, 500000, 750000, 1000000),
    Sulphur("SULPHUR_ORE", 200, 1000, 2500, 5000, 10000, 15000, 25000, 50000, 100000),
    Netherrack("NETHERRACK", 50, 250, 500, 1000, 5000),

    // Combat
    Ender_Pearl("ENDER_PEARL", 50, 250, 1000, 2500, 5000, 10000, 15000, 25000, 50000),
    Chili_Pepper("CHILI_PEPPER", 10, 25, 75, 250, 1000, 2500, 5000, 10000, 20000),
    Slimeball("SLIME_BALL", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Magma_Cream("MAGMA_CREAM", 50, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Ghast_Tear("GHAST_TEAR", 20, 250, 1000, 2500, 5000, 10000, 25000),
    Gunpowder("SULPHUR", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Rotten_Flesh("ROTTEN_FLESH", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000),
    Spider_Eye("SPIDER_EYE", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    Bone("BONE", 50, 100, 250, 500, 1000, 5000, 10000, 25000, 50000, 150000),
    Blaze_Rod("BLAZE_ROD", 50, 250, 1000, 2500, 5000, 10000, 25000, 50000),
    String("STRING", 50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000),

    // Foraging
    Acacia_Wood("LOG_2", 50, 100, 250, 500, 1000, 2000, 5000, 10000, 25000),
    Spruce_Wood("LOG:1", 50, 100, 250, 1000, 2000, 5000, 10000, 25000, 50000),
    Jungle_Wood("LOG:3", 50, 100, 250, 500, 1000, 2000, 5000, 10000, 25000),
    Birch_Wood("LOG:2", 50, 100, 250, 500, 1000, 2000, 5000, 10000, 25000, 50000),
    Oak_Wood("LOG", 50, 100, 250, 500, 1000, 2000, 5000, 10000, 30000),
    Dark_Oak_Wood("LOG_2:1", 50, 100, 250, 1000, 2000, 5000, 10000, 15000, 25000),

    // Fishing
    Lily_Pad("WATER_LILY", 10, 50, 100, 200, 500, 1500, 3000, 6000, 10000),
    Prismarine_Shard("PRISMARINE_SHARD", 10, 25, 50, 100, 200, 400, 800),
    Ink_Sac("INK_SACK", 20, 40, 100, 200, 400, 800, 1500, 2500, 4000),
    Raw_Fish("RAW_FISH", 20, 50, 100, 250, 500, 1000, 2500, 15000, 30000, 45000, 60000),
    Pufferfish("RAW_FISH:3", 20, 50, 100, 150, 400, 800, 2400, 4800, 9000, 18000),
    Clownfish("RAW_FISH:2", 10, 25, 50, 100, 200, 400, 800, 1600, 4000),
    Raw_Salmon("RAW_FISH:1", 20, 50, 100, 250, 500, 1000, 2500, 5000, 10000),
    Magmafish("MAGMA_FISH", 20, 100, 500, 1000, 5000, 15000, 30000, 50000, 75000, 100000, 250000, 500000),
    Prismarine_Crystals("PRISMARINE_CRYSTALS", 10, 25, 50, 100, 200, 400, 800),
    Clay("CLAY_BALL", 50, 100, 250, 1000, 1500, 2500),
    Sponge("SPONGE", 20, 40, 100, 200, 400, 800, 1500, 2500, 4000),

    // Rift
    Wilted_Berberis("WILTED_BERBERIS", 20, 60, 140, 400),
    Living_Metal_Heart("METAL_HEART", 1, 20, 60, 100),
    Caducous_Stem("CADUCOUS_STEM", 20, 60, 150, 500),
    Agaricus_Cap("AGARICUS_CAP", 20, 60, 100, 200),
    Hemovibe("HEMOVIBE", 50, 250, 1000, 5000, 15000, 30000, 80000, 150000, 400000),
    Half_Eaten_Carrot("HALF_EATEN_CARROT", 1, 400, 1000, 3500),


            ;

    public final String id;
    public final int[] tiers;

    Collections(String id,int... tiers) {
        this.id=id;
        this.tiers=tiers;
    }

    public String getId() {
        return id;
    }

    public int getTierCount() {
        return tiers.length;
    }

    public int getTierWithCollection(int amount){
        for (int i = this.tiers.length - 1; i >= 0; i--) {
            if (tiers[i]>=amount) return i;
        }
        return 0;
    }

    public int getCollectionForTier(int tier){
        return tiers[Math.min(tier,tiers.length-1)];
    }
}