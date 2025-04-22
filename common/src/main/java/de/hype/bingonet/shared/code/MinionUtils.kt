package de.hype.bingonet.shared.code

import de.hype.bingonet.shared.constants.MinionResourceItem
import de.hype.bingonet.shared.objects.minions.Fueles
import de.hype.bingonet.shared.objects.minions.MinionItem
import de.hype.bingonet.shared.objects.minions.MinionStorage
import de.hype.bingonet.shared.objects.minions.Minions
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Map
import kotlin.math.ceil

object MinionUtils {
    @JvmStatic
    fun getFillTime(
        minion: Minions,
        fuel: Fueles?,
        storage: MinionStorage?,
        item1: MinionItem?,
        item2: MinionItem?,
        percentageMinionSpeed: Double,
        dropMultiplierBoost: Double
    ): Duration? {
        var usedSlots = 0
        var storageUsedAmount = 0

        var itemDropMultiplier = 1.0
        if (fuel != null) itemDropMultiplier *= (fuel.getBoostMultiplier() - 1).toDouble()
        if (dropMultiplierBoost != 0.0) itemDropMultiplier *= dropMultiplierBoost
        if (item1 != null) itemDropMultiplier *= item1.getMultiplier(minion)
        if (item2 != null) itemDropMultiplier *= item2.getMultiplier(minion)

        var additionalMinionSpeed = 0.0
        if (percentageMinionSpeed != 0.0) additionalMinionSpeed += percentageMinionSpeed
        if (fuel != null) itemDropMultiplier += fuel.boostPercentage.toDouble()
        if (item1 != null) additionalMinionSpeed += item1.getMinionSpeedAdditive(minion).toDouble()
        if (item2 != null) additionalMinionSpeed += item2.getMinionSpeedAdditive(minion).toDouble()

        val slots = minion.getStorageSlots() + (storage?.storageSlots ?: 0)
        val compactor = (item1 === MinionItem.COMPACTOR || item2 === MinionItem.COMPACTOR)
        val values =
            minion.getItems().entries.stream().sorted(Map.Entry.comparingByValue<MinionResourceItem?, Double>())
                .toList()
        for (i1 in 0..<values.size - 1) {
            var item = values[i1]!!.key
            if (item1 != null) item = item1.convertItem(item)
            if (item2 != null) item = item2.convertItem(item)
            var usedNow = ((values[i1]!!.value * ((slots - usedSlots) * 64)) * itemDropMultiplier).toInt()
            if (compactor) {
                usedNow = ceil(usedNow.toDouble() / item.compactorLevel).toInt()
                usedSlots++
            }
            storageUsedAmount += usedNow
            usedSlots += (ceil(usedNow.toDouble() / 64.0).toInt())
        }
        storageUsedAmount += 64 * (slots - usedSlots)

        val estimation =
            (((100f / (100f + additionalMinionSpeed)) * (minion.delay * minion.actionsForItem * itemDropMultiplier) * (storageUsedAmount - 63)).toInt())
        return Duration.of(estimation.toLong(), ChronoUnit.SECONDS)
    }

    @JvmStatic
    fun getHourlyDrops(
        minion: Minions,
        minionCount: Int,
        fuel: Fueles?,
        storage: MinionStorage?,
        item1: MinionItem?,
        item2: MinionItem?,
        percentageMinionSpeed: Double?,
        dropMultiplierBoost: Double?
    ): MutableMap<MinionResourceItem?, Double?> {
        if (minionCount == 0) return HashMap()

        var itemDropMultiplier = 1.0
        if (fuel != null) itemDropMultiplier *= (fuel.getBoostMultiplier() - 1).toDouble()
        if (dropMultiplierBoost != null && dropMultiplierBoost != 0.0) itemDropMultiplier *= dropMultiplierBoost
        if (item1 != null) itemDropMultiplier *= item1.getMultiplier(minion)
        if (item2 != null) itemDropMultiplier *= item2.getMultiplier(minion)

        var additionalMinionSpeed = 0.0
        if (percentageMinionSpeed != null && percentageMinionSpeed != 0.0) additionalMinionSpeed += percentageMinionSpeed
        if (fuel != null) itemDropMultiplier += fuel.boostPercentage.toDouble()
        if (item1 != null) additionalMinionSpeed += item1.getMinionSpeedAdditive(minion).toDouble()
        if (item2 != null) additionalMinionSpeed += item2.getMinionSpeedAdditive(minion).toDouble()

        val actions =
            3600 / ((100f / (100f + additionalMinionSpeed)) * (minion.delay * minion.actionsForItem))

        val generated: MutableMap<MinionResourceItem?, Double?> = HashMap()
        val finalItemDropMultiplier = itemDropMultiplier
        minion.getItems().forEach { (k: MinionResourceItem, v: Double) ->
            var item = k
            if (item1 != null) item = item1.convertItem(item)
            if (item2 != null) item = item2.convertItem(item)
            generated.put(item, actions * v * finalItemDropMultiplier * minionCount)
        }

        val sum = generated.values.stream().mapToDouble { v: Double? -> v!! }.sum()

        item1?.modifyDrops(sum, generated)
        item2?.modifyDrops(sum, generated)
        return generated
    }
}
