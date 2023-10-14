package de.hype.bbsentials.common.constants.enviromentShared;

import de.hype.bbsentials.common.constants.BBDisplayNameProvider;

// Mining Events
public enum MiningEvents implements BBDisplayNameProvider {
    BETTER_TOGETHER("Better Together"),
    DOUBLE_POWDER("Double Powder"),
    GONE_WITH_THE_WIND("Gone with the Wind"),
    GOBLIN_RAID("Goblin Raid"),
    MITHRIL_GOURMAND("Mithril Gourmand"),
    RAFFLE("Raffle");

    private final String displayName;

    MiningEvents(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    //Some Events cant happen in Crystal Holows
    public boolean isDWEventOnly() {
        if (this.equals(MiningEvents.MITHRIL_GOURMAND) || this.equals(MiningEvents.RAFFLE) || this.equals(MiningEvents.GOBLIN_RAID)) {
            return true;
        }
        return false;
    }

    public static boolean isDWEventOnly(String event) {
        if (event.equals(MiningEvents.MITHRIL_GOURMAND.getDisplayName()) || event.equals(MiningEvents.RAFFLE.getDisplayName()) || event.equals(MiningEvents.GOBLIN_RAID.getDisplayName())) {
            return true;
        }
        return false;
    }
}
