package de.hype.bbsentials.shared.code;

import de.hype.bbsentials.shared.constants.MinionResourceItem;
import de.hype.bbsentials.shared.objects.minions.Fueles;
import de.hype.bbsentials.shared.objects.minions.MinionItem;
import de.hype.bbsentials.shared.objects.minions.MinionStorage;
import de.hype.bbsentials.shared.objects.minions.Minions;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinionUtils {


    public static Duration getFillTime(Minions minion, Fueles fuel, MinionStorage storage, MinionItem item1, MinionItem item2, Double percentageMinionSpeed, Double dropMultiplierBoost) {
        int usedSlots = 0;
        int storageUsedAmount = 0;

        double itemDropMultiplier = 1;
        if (fuel != null) itemDropMultiplier *= fuel.getBoostMultiplier() - 1;
        if (dropMultiplierBoost != null && dropMultiplierBoost != 0)
            itemDropMultiplier *= dropMultiplierBoost;
        if (item1 != null) itemDropMultiplier *= item1.getMultiplier(minion);
        if (item2 != null) itemDropMultiplier *= item2.getMultiplier(minion);

        double additionalMinionSpeed = 0;
        if (percentageMinionSpeed != null && percentageMinionSpeed != 0)
            additionalMinionSpeed += percentageMinionSpeed;
        if (fuel != null) itemDropMultiplier += fuel.getBoostPercentage();
        if (item1 != null) additionalMinionSpeed += item1.getMinionSpeedAdditive(minion);
        if (item2 != null) additionalMinionSpeed += item2.getMinionSpeedAdditive(minion);

        int slots = minion.getStorageSlots() + storage.getStorageSlots();
        boolean compactor = (item1 == MinionItem.COMPACTOR || item2 == MinionItem.COMPACTOR);
        List<Map.Entry<MinionResourceItem, Double>> values = minion.getItems().entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
        for (int i1 = 0; i1 < values.size() - 1; i1++) {
            MinionResourceItem item = values.get(i1).getKey();
            if (item1 != null) item = item1.convertItem(item);
            if (item2 != null) item = item2.convertItem(item);
            int usedNow = (int) ((values.get(i1).getValue() * ((slots - usedSlots) * 64)) * itemDropMultiplier);
            if (compactor) {
                usedNow = (int) Math.ceil((double) usedNow / item.getCompactorLevel());
                usedSlots++;
            }
            storageUsedAmount += usedNow;
            usedSlots += ((int) Math.ceil((double) usedNow / 64D));
        }
        storageUsedAmount += 64 * (slots - usedSlots);

        int estimation = ((int) ((100F / (100F + additionalMinionSpeed)) * (minion.getDelay() * minion.getActionsForItem() * itemDropMultiplier) * (storageUsedAmount - 63)));
        return Duration.of(estimation, ChronoUnit.SECONDS);
    }

    public static Map<MinionResourceItem, Double> getHourlyDrops(Minions minion, int minionCount, Fueles fuel, MinionStorage storage, MinionItem item1, MinionItem item2, Double percentageMinionSpeed, Double dropMultiplierBoost) {
        if (minionCount == 0) return new HashMap<>();

        double itemDropMultiplier = 1;
        if (fuel != null) itemDropMultiplier *= fuel.getBoostMultiplier() - 1;
        if (dropMultiplierBoost != null && dropMultiplierBoost != 0)
            itemDropMultiplier *= dropMultiplierBoost;
        if (item1 != null) itemDropMultiplier *= item1.getMultiplier(minion);
        if (item2 != null) itemDropMultiplier *= item2.getMultiplier(minion);

        double additionalMinionSpeed = 0;
        if (percentageMinionSpeed != null && percentageMinionSpeed != 0)
            additionalMinionSpeed += percentageMinionSpeed;
        if (fuel != null) itemDropMultiplier += fuel.getBoostPercentage();
        if (item1 != null) additionalMinionSpeed += item1.getMinionSpeedAdditive(minion);
        if (item2 != null) additionalMinionSpeed += item2.getMinionSpeedAdditive(minion);

        double actions = 3600 / ((100F / (100F + additionalMinionSpeed)) * (minion.getDelay() * minion.getActionsForItem()));

        Map<MinionResourceItem, Double> generated = new HashMap<>();
        double finalItemDropMultiplier = itemDropMultiplier;
        minion.getItems().forEach((k, v) -> {
            MinionResourceItem item = k;
            if (item1 != null) item = item1.convertItem(item);
            if (item2 != null) item = item2.convertItem(item);
            generated.put(item, actions * v * finalItemDropMultiplier * minionCount);
        });

        double sum = generated.values().stream().mapToDouble(v -> v).sum();

        if (item1 != null) item1.modifyDrops(sum, generated);
        if (item2 != null) item2.modifyDrops(sum, generated);
        return generated;

    }

}
