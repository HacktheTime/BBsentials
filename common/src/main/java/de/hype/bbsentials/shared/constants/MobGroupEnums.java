package de.hype.bbsentials.shared.constants;

public enum MobGroupEnums {
    Zombie("Zombie"),
    Wolf("Wolf"),
    Chicken("Chicken"),
    Cow("Cow"),
    Pig("Pig"),
    Rabbit("Rabbit"),
    Sheep("Sheep"),
    Arachne("Arachne"),
    Broodmother("Broodmother"),
    Dasher_Spider("Dasher Spider"),
    Skeleton("Skeleton"),
    Rain_Slime("Rain Slime"),
    Silverfish("Silverfish"),
    Spider_Jockey("Spider Jockey"),
    Splitter_Spider("Splitter Spider"),
    Voracious_Spider("Voracious Spider"),
    Weaver_Spider("Weaver Spider"),
    Enderman("Enderman"),
    Endermite("Endermite"),
    Obsidian_Defender("Obsidian Defender"),
    Watcher("Watcher"),
    Ender_Dragons("Ender Dragons"),
    Endstone_Protector("Endstone_Protector"),
    TEAM_TREASURITE("Team Treasurite"),
    GOBLIN("Goblin"),
    Nether_Bosses("Crimson Isle Minibosses"),

    

    ;
    private String groupDisplayName;
    private List<MobEnums> mobs = new ArrayList<>();

    MobGroupEnums(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public List<MobEnums> getMobs() {
        if (mobs != null) return new ArrayList<>(mobs);
        List<MobEnums> mobs = new ArrayList<>();
        for (MobEnums mob : MobEnums.values()) {
            if (mob.getMobGroup().equals(this)) {
                mobs.add(mob);
            }
        }
        this.mobs = new ArrayList<>(mobs);
        return mobs;
    }

    public MobEnums[] getMobs() {
        return mobEnums;
    }
}
