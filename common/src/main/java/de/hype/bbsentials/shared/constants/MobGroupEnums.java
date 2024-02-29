package de.hype.bbsentials.shared.constants;

public enum MobGroupEnums {
    Zombie("Zombie",MobEnums.Graveyard_Zombie, MobEnums.Zombie_Villager, MobEnums.Crypt_Ghoul, MobEnums.Golden_Ghoul),
    Wolf("Wolf", MobEnums.Wolf, MobEnums.Old_Wolf),
    Chicken("Chicken", MobEnums.Chicken),
    Cow("Cow", MobEnums.Cow, MobEnums.Mushroom_Cow),
    Pig("Pig", MobEnums.Pig),
    Rabbit("Rabbit", MobEnums.Rabbit),
    Sheep("Sheep", MobEnums.Sheep),
    Arachne("Arachne", MobEnums.Arachne_300, MobEnums.Arachne_500, MobEnums.Arachne_Brood_100, MobEnums.Arachne_Brood_200, MobEnums.Arachne_Keeper_100),
    Broodmother("Broodmother", MobEnums.Broodmother),
    Dasher_Spider("Dasher Spider", MobEnums.Dasher_Spider_4, MobEnums.Dasher_Spider_6, MobEnums.Dasher_Spider_42, MobEnums.Dasher_Spider_45, MobEnums.Dasher_Spider_50),
    Skeleton("Skeleton", MobEnums.Gravel_Skeleton),
    Rain_Slime("Rain Slime", MobEnums.Rain_Slime_8, MobEnums.Rain_Slime_20),
    Silverfish("Silverfish", MobEnums.Jockey_Shot_Silverfish_3, MobEnums.Jockey_Shot_Silverfish_42, MobEnums.Splitter_Spider_Silverfish_2, MobEnums.Splitter_Spider_Silverfish_42, MobEnums.Splitter_Spider_Silverfish_45, MobEnums.Splitter_Spider_Silverfish_50),
    Spider_Jockey("Spider Jockey", MobEnums.Spider_Jockey_3, MobEnums.Spider_Jockey_5, MobEnums.Spider_Jockey_42),
    Splitter_Spider("Splitter Spider", MobEnums.Splitter_Spider_2, MobEnums.Splitter_Spider_4, MobEnums.Splitter_Spider_6, MobEnums.Splitter_Spider_4, MobEnums.Splitter_Spider_45, MobEnums.Splitter_Spider_50),
    Voracious_Spider("Voracious Spider", MobEnums.Voracious_Spider_10, MobEnums.Voracious_Spider_42, MobEnums.Voracious_Spider_45, MobEnums.Voracious_Spider_50),
    Weaver_Spider("Weaver Spider", MobEnums.Weaver_Spider_3, MobEnums.Weaver_Spider_4, MobEnums.Weaver_Spider_5, MobEnums.Weaver_Spider_6, MobEnums.Weaver_Spider_42, MobEnums.Weaver_Spider_45, MobEnums.Weaver_Spider_50),
    Enderman("Enderman", MobEnums.Enderman_42, MobEnums.Enderman_45, MobEnums.Enderman_50, MobEnums.Voidling_Extremist, MobEnums.Voidling_Fanatic, MobEnums.Zealot_Enderman, MobEnums.Zealot_Bruiser),
    Endermite("Endermite", MobEnums.Nest_Endermite_50, MobEnums.Endermite_37, MobEnums.Endermite_40),
    Obsidian_Defender("Obsidian Defender", MobEnums.Obsidian_Defender),
    Watcher("Watcher", MobEnums.Watcher),
    Ender_Dragons("Ender Dragons", MobEnums.Protector_Dragon, MobEnums.Old_Dragon, MobEnums.Young_Dragon, MobEnums.Wise_Dragon, MobEnums.Superior_Dragon, MobEnums.Strong_Dragon, MobEnums.Unstable_Dragon),
    Endstone_Protector("Endstone_Protector", MobEnums.Endstone_Protector),
    TEAM_TREASURITE("Team Treasurite", null),
    GOBLIN("Goblin", MobEnums.Golden_Goblin),
    Nether_Bosses("Crimson Isle Minibosses", MobEnums.Ashfang_200, MobEnums.Magma_Boss_500, MobEnums.Mage_Outlaw_200, MobEnums.Barbarian_Duke_x_200, MobEnums.Blade_Soul_200),

    

    ;   
    private final MobEnums[] mobEnums;
    public final String groupDisplayName;

    MobGroupEnums(String groupDisplayName, MobEnums... mobEnums) {
        this.groupDisplayName = groupDisplayName;
        this.mobEnums = mobEnums;
    }

    public MobEnums[] getMobs() {
        return mobEnums;
    }
}
