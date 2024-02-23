package de.hype.bbsentials.shared.constants;

public enum MobEnums {
    Graveyard_Zombie("graveyard_zombie_1", "Graveyard Zombie", 1, MobGroupEnums.Zombie, Islands.HUB),
    Zombie_Villager("zombie_villager_1", "Zombie Villager", 1, MobGroupEnums.Zombie, Islands.HUB),
    Crypt_Ghoul("unburried_zombie_30", "Crypt Ghoul", 30, MobGroupEnums.Zombie, Islands.HUB),
    Golden_Ghoul("unburried_zombie_60", "Golden Ghoul", 60, MobGroupEnums.Zombie, Islands.HUB),
//    Wolf("ruin_wolf_15", "Wolf", 15, MobGroupEnums.Wolf, Islands.HUB),
    Enderman_42("enderman_42", "Enderman", 42, MobGroupEnums.Enderman, Islands.THE_END),
    Enderman_45("enderman_45", "Enderman", 45, MobGroupEnums.Enderman, Islands.THE_END),
    Enderman_50("enderman_50", "Enderman", 50, MobGroupEnums.Enderman, Islands.THE_END),
    Golden_Goblin("goblin_50", "Golden Goblin", 50, MobGroupEnums.GOBLIN, Islands.DWARVEN_MINES, Islands.CRYSTAL_HOLLOWS),
    Ashfang_200("ashfang_200", "Ashfang", 200, MobGroupEnums.Nether_Bosses, Islands.CRIMSON_ISLE),
    Barbarian_Duke_x_200("barbarian_duke_x_200", "Barbarian Duke X", 200, MobGroupEnums.Nether_Bosses, Islands.CRIMSON_ISLE),
    Mage_Outlaw_200("mage_outlaw_200", "Mage Outlaw", 200, MobGroupEnums.Nether_Bosses, Islands.CRIMSON_ISLE),
    Blade_Soul_200("blade_soul_200", "Blade Soul", 200, MobGroupEnums.Nether_Bosses, Islands.CRIMSON_ISLE),
    Magma_Boss_500("magma_boss_500", "Magma Boss", 500, MobGroupEnums.Nether_Bosses, Islands.CRIMSON_ISLE),


    ;

    public final String id;
    public final String displayName;
    public final int level;
    public final MobGroupEnums mobGroup;
    public final Islands[] island;


    MobEnums(String id, String displayName, int level, MobGroupEnums mobGroup, Islands... island) {
        this.id = id;
        this.displayName = displayName;
        this.level = level;
        this.mobGroup = mobGroup;
        this.island = island;
    }
}
