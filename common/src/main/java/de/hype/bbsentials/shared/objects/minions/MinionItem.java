package de.hype.bbsentials.shared.objects.minions;

import de.hype.bbsentials.shared.constants.Collections;
import de.hype.bbsentials.shared.constants.MinionResourceItem;

import java.util.Map;

public enum MinionItem {
    AUTO_SMELTER("Auto Smelter") {
        @Override
        public MinionResourceItem convertItem(MinionResourceItem item) {
            if (item instanceof Collections.Foraging) return Collections.Mining.Coal;
            return super.convertItem(item);
        }
    },
    COMPACTOR("Compactor"),
    SUPER_COMPACTOR_3000("Super Compactor 3000"),
    DWARFEN_SUPER_COMPACTOR("Dwarfen Super Compactor") {
        @Override
        public MinionResourceItem convertItem(MinionResourceItem item) {
            return AUTO_SMELTER.convertItem(item);
        }
    },
    DIAMOND_SPREADING("Diamond Spreading") {
        @Override
        public void modifyDrops(double sum, Map<MinionResourceItem, Double> generated) {
            generated.put(Collections.Mining.Diamond, generated.getOrDefault(Collections.Mining.Diamond, 0D) + (sum / 10));
        }
    },
    POTATO_SPREADING(false, "Potato Spreading") {
        @Override
        public void modifyDrops(double sum, Map<MinionResourceItem, Double> generated) {
            generated.put(Collections.Farming.Potato, generated.getOrDefault(Collections.Farming.Potato, 0D) + (sum / 20));
        }
    },
    MINION_EXPANDER("Minion Expander") {
        @Override
        public Integer getMinionSpeedAdditive(Minions minion) {
            return 5;
        }
    },
    ENCHANTED_EGG("Enchanted Egg"),
    FLINT_SHOVEL("Flint Shovel"),
    FLYCATCHER(false, "Flycatcher") {
        @Override
        public Integer getMinionSpeedAdditive(Minions minion) {
            return 20;
        }
    },
    KRAMPUS_HELMET(false, "Krampus Helmet") {
        @Override
        public void modifyDrops(double sum, Map<MinionResourceItem, Double> generated) {
            generated.put(MinionResourceItem.UnusedMinionItems.RED_GIFT, generated.getOrDefault(MinionResourceItem.UnusedMinionItems.RED_GIFT, 0D) + (sum * 0.000045));
        }
    },
    LESSER_SOULFLOW_ENGINE("Lesser Soulflow Engine") {
        @Override
        public Map<MinionResourceItem, Integer> itemsGenerated(Map<MinionResourceItem, Integer> dropsGenerated, Integer minionActions, Minions minion) {
            for (Map.Entry<MinionResourceItem, Integer> collectionsIntegerEntry : dropsGenerated.entrySet()) {
                dropsGenerated.put(collectionsIntegerEntry.getKey(), collectionsIntegerEntry.getValue() / 2);
            }
            return dropsGenerated;
        }
    },
    SOULFLOW_ENGINE("Soulflow Engine") {
        @Override
        public double getMultiplier(Minions minion) {
            return 0.5;
        }
    },
    CORRUPT_SOIL("Corrupt Soil") {
        @Override
        public Map<MinionResourceItem, Integer> itemsGenerated(Map<MinionResourceItem, Integer> dropsGenerated, Integer minionActions, Minions minion) {
            if (minion.spawnsMobs()) dropsGenerated.put(Collections.Mining.Sulphur, minionActions);
            return dropsGenerated;
        }
    },
    ENCHANTED_SHEARS("Enchanted Shears"),
    BERBERIES_FUEL_INJECTOR("Berberis Fuel Injector") {
        @Override
        public Integer getMinionSpeedAdditive(Minions minion) {
            if (minion.getType() == MinionType.FARMING) return 15;
            return 0;
        }
    },
    SLEEPY_HOLLOW(false, "Sleepy Hollow") {
        @Override
        public Map<MinionResourceItem, Integer> itemsGenerated(Map<MinionResourceItem, Integer> dropsGenerated, Integer minionActions, Minions minion) {
            //dropsGenerated.put(PURPLE_CANY,(int) dropsGenerated.values().stream().mapToInt(v->v).sum()*0.00015);
            return dropsGenerated;
        }
    };

    public String displayName;
    public boolean bingoObtainable;


    MinionItem(String name) {
        this(true, name);
    }

    MinionItem(boolean bingoObtainable, String name) {
        this.displayName = name;
        this.bingoObtainable = bingoObtainable;
    }

    public Integer getMinionSpeedAdditive(Minions minion) {
        return 0;
    }

    public Map<MinionResourceItem, Integer> itemsGenerated(Map<MinionResourceItem, Integer> dropsGenerated, Integer minionActions, Minions minion) {
        return dropsGenerated;
    }

    public Map<MinionResourceItem, Integer> applyCompacting(Map<MinionResourceItem, Integer> drops, Minions minion) {
        return drops;
    }


    public MinionResourceItem convertItem(MinionResourceItem item) {
        return item;
    }

    public double getMultiplier(Minions minion) {
        return 1;
    }

    public void modifyDrops(double sum, Map<MinionResourceItem, Double> generated) {
    }
}
