package de.hype.bingonet.shared.objects.minions

import de.hype.bingonet.shared.constants.Collections
import de.hype.bingonet.shared.constants.Collections.Foraging
import de.hype.bingonet.shared.constants.MinionResourceItem

enum class MinionItem(var bingoObtainable: Boolean, var displayName: String) {
    AUTO_SMELTER("Auto Smelter") {
        override fun convertItem(item: MinionResourceItem): MinionResourceItem {
            if (item is Foraging) return Collections.Mining.Coal
            return super.convertItem(item)
        }
    },
    COMPACTOR("Compactor"),
    SUPER_COMPACTOR_3000("Super Compactor 3000"),
    DWARFEN_SUPER_COMPACTOR("Dwarfen Super Compactor") {
        override fun convertItem(item: MinionResourceItem): MinionResourceItem {
            return AUTO_SMELTER.convertItem(item)
        }
    },
    DIAMOND_SPREADING("Diamond Spreading") {
        override fun modifyDrops(sum: Double, generated: MutableMap<MinionResourceItem, Double>) {
            generated.put(
                Collections.Mining.Diamond,
                generated.getOrDefault(Collections.Mining.Diamond, 0.0) + (sum / 10)
            )
        }
    },
    POTATO_SPREADING(false, "Potato Spreading") {
        override fun modifyDrops(sum: Double, generated: MutableMap<MinionResourceItem, Double>) {
            generated.put(
                Collections.Farming.Potato,
                generated.getOrDefault(Collections.Farming.Potato, 0.0) + (sum / 20)
            )
        }
    },
    MINION_EXPANDER("Minion Expander") {
        override fun getMinionSpeedAdditive(minion: Minions): Int {
            return 5
        }
    },
    ENCHANTED_EGG("Enchanted Egg"),
    FLINT_SHOVEL("Flint Shovel"),
    FLYCATCHER(false, "Flycatcher") {
        override fun getMinionSpeedAdditive(minion: Minions): Int {
            return 20
        }
    },
    KRAMPUS_HELMET(false, "Krampus Helmet") {
        override fun modifyDrops(sum: Double, generated: MutableMap<MinionResourceItem, Double>) {
            generated.put(
                MinionResourceItem.UnusedMinionItems.RED_GIFT,
                generated.getOrDefault(MinionResourceItem.UnusedMinionItems.RED_GIFT, 0.0) + (sum * 0.000045)
            )
        }
    },
    LESSER_SOULFLOW_ENGINE("Lesser Soulflow Engine") {
        override fun items(
            dropsGenerated: MutableMap<MinionResourceItem, Int>,
            minionActions: Int,
            minion: Minions
        ): MutableMap<MinionResourceItem, Int> {
            for (collectionsIntegerEntry in dropsGenerated.entries) {
                dropsGenerated.put(collectionsIntegerEntry.key, collectionsIntegerEntry.value / 2)
            }
            return dropsGenerated
        }
    },
    SOULFLOW_ENGINE("Soulflow Engine") {
        override fun getMultiplier(minion: Minions): Double {
            return 0.5
        }
    },
    CORRUPT_SOIL("Corrupt Soil") {
        override fun items(
            dropsGenerated: MutableMap<MinionResourceItem, Int>,
            minionActions: Int,
            minion: Minions
        ): MutableMap<MinionResourceItem, Int> {
            if (minion.spawnsMobs()) dropsGenerated.put(Collections.Mining.Sulphur, minionActions)
            return dropsGenerated
        }
    },
    ENCHANTED_SHEARS("Enchanted Shears"),
    BERBERIES_FUEL_INJECTOR("Berberis Fuel Injector") {
        override fun getMinionSpeedAdditive(minion: Minions): Int {
            if (minion.getType() == MinionType.FARMING) return 15
            return 0
        }
    },
    SLEEPY_HOLLOW(false, "Sleepy Hollow") {
        override fun items(
            dropsGenerated: MutableMap<MinionResourceItem, Int>,
            minionActions: Int,
            minion: Minions
        ): MutableMap<MinionResourceItem, Int> {
            //dropsGenerated.put(PURPLE_CANY,(int) dropsGenerated.values().mapToInt(v->v).sum()*0.00015);
            return dropsGenerated
        }
    };


    constructor(name: String) : this(true, name)

    open fun getMinionSpeedAdditive(minion: Minions): Int {
        return 0
    }

    open fun items(
        dropsGenerated: MutableMap<MinionResourceItem, Int>,
        minionActions: Int,
        minion: Minions
    ): MutableMap<MinionResourceItem, Int> {
        return dropsGenerated
    }

    fun applyCompacting(
        drops: MutableMap<MinionResourceItem, Int>,
        minion: Minions
    ): MutableMap<MinionResourceItem, Int> {
        return drops
    }


    open fun convertItem(item: MinionResourceItem): MinionResourceItem {
        return item
    }

    open fun getMultiplier(minion: Minions): Double {
        return 1.0
    }

    open fun modifyDrops(sum: Double, generated: MutableMap<MinionResourceItem, Double>) {}
}
