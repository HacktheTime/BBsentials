package de.hype.bbsentials.shared.constants;

public enum MobGroupEnums {
    Zombie("Zombie",MobEnums.Graveyard_Zombie, MobEnums.Zombie_Villager, MobEnums.Crypt_Ghoul, MobEnums.Golden_Ghoul),
//    Wolf("Wolf", MobEnums.Wolf, MobEnums.Old_Wolf),
    Enderman("Enderman", MobEnums.Enderman_42, MobEnums.Enderman_45, MobEnums.Enderman_50),
//    TEAM_TREASURITE("Team Treasurite", MobEnums.Team),
    GOBLIN("Goblin", MobEnums.Golden_Goblin),
    Nether_Bosses("Crimson Isle Minibosses", MobEnums.Ashfang_200, MobEnums.Magma_Boss_500, MobEnums.Mage_Outlaw_200, MobEnums.Barbarian_Duke_x_200, MobEnums.Blade_Soul_200),
    Ender_Dragons("Ender Dragons", null),
    

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
