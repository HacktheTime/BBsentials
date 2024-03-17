package de.hype.bbsentials.shared.constants;

import java.util.ArrayList;
import java.util.List;

public enum MobGroupEnums {
    Zombie("Zombie"),
    Wolf("Wolf"),
    Chicken("Chicken"),
    Cow("Cow"),
    Pig("Pig"),
    Rabbit("Rabbit"),
    Sheep("Sheep"),
    Spider("Spider"),
    Arachne_Boss("Arachne Boss"),
    Broodmother("Broodmother"),
    Skeleton("Skeleton"),
    Rain_Slime("Rain Slime"),
    Silverfish("Silverfish"),
    Enderman("Enderman"),
    Endermite("Endermite"),
    Obsidian_Defender("Obsidian Defender"),
    Watcher("Watcher"),
    Ender_Dragons("Ender Dragons"),
    Endstone_Protector("Endstone_Protector"),
    TEAM_TREASURITE("Team Treasurite"),
    GOBLIN("Goblin"),
    Nether_Bosses("Crimson Isle Minibosses"),
    Blaze("Blaze"),

    

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
}
