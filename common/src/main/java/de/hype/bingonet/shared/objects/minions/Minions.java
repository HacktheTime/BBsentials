package de.hype.bingonet.shared.objects.minions;

import de.hype.bingonet.shared.constants.Collections;
import de.hype.bingonet.shared.constants.MinionResourceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Minions {
    Map<String, Class<? extends Minions>> minionStringMap = new HashMap<>();

    private static void indexMinions() {
        for (Class<?> clazz : Minions.class.getClasses()) {
            if (!Minions.class.isAssignableFrom(clazz)) continue;
            minionStringMap.put(clazz.getSimpleName().toLowerCase(), (Class<? extends Minions>) clazz);
        }
    }

    static Minions getMinionFromString(String minionName, String tier) throws Exception {
        if (minionStringMap.isEmpty()) indexMinions();
        // Get the class associated with the minion name
        Class<? extends Minions> clazz = minionStringMap.get(minionName.toLowerCase().replace("minion", "").trim());
        if (clazz == null) {
            throw new Exception("No Minion With that Name Found.");
        }

        // Check if the class is an enum and implements Minions
        if (Minions.class.isAssignableFrom(clazz) && clazz.isEnum()) {
            try {
                // Get the enum constants
                Object[] enumConstants = clazz.getEnumConstants();
                String normalizedTier;

                // Check if the tier is a valid number
                try {
                    int tierValue = Integer.parseInt(tier);
                    // Check for out-of-bounds values
                    if (tierValue < 1 || tierValue > 12) {
                        throw new Exception("Tier must be between 1 and 12.");
                    }
                    // Convert the Arabic numeral to Roman numeral
                    normalizedTier = convertToRoman(tierValue);
                } catch (NumberFormatException e) {
                    // If it's not a number, treat it as a Roman numeral
                    normalizedTier = tier.toUpperCase();
                    // Check if the Roman numeral is valid and within bounds
                    if (!isValidRomanNumeral(normalizedTier)) {
                        throw new Exception("There only are tiers from I to XII.");
                    }
                }

                // Iterate through the enum constants to find the matching tier
                for (Object enumConstant : enumConstants) {
                    if (enumConstant instanceof Enum) {
                        Enum<?> enumValue = (Enum<?>) enumConstant;
                        // Check if the normalized tier matches the enum name
                        if (enumValue.name().equalsIgnoreCase(normalizedTier)) {
                            return (Minions) enumValue; // Return the matching enum constant
                        }
                    }
                }
                throw new Exception("No Tier with that name found.");
            } catch (Exception e) {
                throw new Exception("Error retrieving Minion: " + e.getMessage());
            }
        }

        throw new Exception("The specified class does not implement Minions or is not an enum.");
    }

    public static List<String> getAllMinions() {
        if (minionStringMap.isEmpty()) indexMinions();
        return new ArrayList<>(minionStringMap.keySet());
    }

    // Method to convert Arabic numeral to Roman numeral (1 to 12)
    private static String convertToRoman(int number) {
        if (number < 1 || number > 12) {
            throw new IllegalArgumentException("Number out of range for Roman numerals (1-12).");
        }

        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 11:
                return "XI";
            case 12:
                return "XII";
            default:
                return ""; // This should never be reached due to the range check
        }
    }

    // Method to check if a Roman numeral is valid and within bounds (I to XII)
    private static boolean isValidRomanNumeral(String roman) {
        switch (roman) {
            case "I":
            case "II":
            case "III":
            case "IV":
            case "V":
            case "VI":
            case "VII":
            case "VIII":
            case "IX":
            case "X":
            case "XI":
            case "XII":
                return true;
            default:
                return false;
        }
    }
    int getTierCost();

    int getDelay();

    default int dropMultiplier(Collections collections) {
        return 1;
    }

    int getStorage();

    int getStorageSlots();

    MinionType getType();

    boolean spawnsMobs();

    default int getActionsForItem() {
        return 2;
    }

    Map<MinionResourceItem, Double> getItems();

    default String getName() {
        return this.getClass().getSimpleName() + " " + getTier();
    }

    int getTier();

    public enum Cobblestone implements Minions {
        I(80, 14, 64),
        II(160, 14, 192),
        III(320, 12, 192),
        IV(512, 12, 384),
        V(1280, 10, 384),
        VI(2560, 10, 576),
        VII(5120, 9, 576),
        VIII(10240, 9, 768),
        IX(20480, 8, 768),
        X(40960, 8, 960),
        XI(81920, 7, 960);

        private final int tierCost;
        private final int delay; // in seconds
        private final int storage;

        Cobblestone(int tierCost, int delay, int storage) {
            this.tierCost = tierCost;
            this.delay = delay;
            this.storage = storage;
        }

        @Override
        public int getTierCost() {
            return tierCost;
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public int getStorage() {
            return storage;
        }

        @Override
        public int getStorageSlots() {
            return storage / 64; // Assuming each storage slot holds 64 items
        }

        @Override
        public MinionType getType() {
            return MinionType.MINING; // Set the type to MINING in uppercase
        }

        @Override
        public boolean spawnsMobs() {
            return false; // Assuming cobblestone minions do not spawn mobs
        }

        @Override
        public java.util.Map<MinionResourceItem, Double> getItems() {
            return java.util.Map.of(Collections.Mining.Cobblestone, 100.0); // Set to 100% for cobblestone
        }

        @Override
        public int getTier() {
            return ordinal() + 1; // Tiers are 1-based, ordinal() is 0-based
        }
    }
}
