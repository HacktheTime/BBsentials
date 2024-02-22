package de.hype.bbsentials.shared.constants;

public enum MobGroupEnums {
//    Zombie("Zombie", MobEnums.Crypt_Ghoul),
//    Wolf("Wolf", MobEnums.Wolf),
//    Enderman("Enderman", MobEnums.Enderman_42),
//    TEAM_TREASURITE("Team Treasurite"),
//    GOBLIN("Team Treasurite"),
//    Nether_Bosses("Crimson Isle Minibosses", MobEnums.Ashfang_200, MobEnums.Magma_Boss_500, MobEnums.Mage_Outlaw_200, MobEnums.Barbarian_Duke_x_200, MobEnums.Blade_Soul_200),
//    Ender_Dragons("Ender Dragons", null),


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
