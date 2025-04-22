package de.hype.bingonet.shared.objects.minions

import de.hype.bingonet.shared.constants.Collections
import de.hype.bingonet.shared.constants.MinionResourceItem
import java.util.*
import java.util.Map

interface Minions {
    val tierCost: Int

    val delay: Int

    fun dropMultiplier(collections: Collections): Int {
        return 1
    }

    val storage: Int

    fun spawnsMobs(): Boolean

    val actionsForItem: Int
        get() = 2

    val minionName: String
        get() = this.javaClass.getSimpleName() + " " + this.getTier()

    fun getType(): MinionType

    fun getStorageSlots(): Int {
        return storage / 64 // Assuming each storage slot holds 64 items
    }

    enum class Cobblestone(
        override val tierCost: Int, // in seconds
        override val delay: Int, override val storage: Int
    ) : Minions {
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


        override fun getType(): MinionType {
            return MinionType.MINING
        }

        override fun spawnsMobs(): Boolean {
            return false
        }

        override fun getItems(): MutableMap<MinionResourceItem, Double> {
            return Map.of<MinionResourceItem, Double>(
                Collections.Mining.Cobblestone,
                100.0
            ) // Set to 100% for cobblestone
        }

        override fun getTier(): Int {
            return ordinal + 1 // Tiers are 1-based, ordinal() is 0-based
        }
    }

    companion object {
        private fun indexMinions() {
            for (clazz in Minions::class.java.getClasses()) {
                if (!Minions::class.java.isAssignableFrom(clazz)) continue
                minionStringMap.put(clazz.getSimpleName().lowercase(Locale.getDefault()), clazz as Class<out Minions?>)
            }
        }

        @JvmStatic
        @Throws(Exception::class)
        fun getMinionFromString(minionName: String, tier: String): Minions {
            if (minionStringMap.isEmpty()) indexMinions()
            // Get the class associated with the minion name
            val clazz: Class<out Minions?> = minionStringMap.get(
                minionName.lowercase(java.util.Locale.getDefault()).replace("minion", "").trim { it <= ' ' })!!
            if (clazz == null) {
                throw Exception("No Minion With that Name Found.")
            }

            // Check if the class is an enum and implements Minions
            if (Minions::class.java.isAssignableFrom(clazz) && clazz.isEnum) {
                try {
                    // Get the enum constants
                    val enumConstants: Array<out Minions> = clazz.getEnumConstants()
                    var normalizedTier: String?

                    // Check if the tier is a valid number
                    try {
                        val tierValue = tier.toInt()
                        // Check for out-of-bounds values
                        if (tierValue < 1 || tierValue > 12) {
                            throw Exception("Tier must be between 1 and 12.")
                        }
                        // Convert the Arabic numeral to Roman numeral
                        normalizedTier = convertToRoman(tierValue)
                    } catch (e: NumberFormatException) {
                        // If it's not a number, treat it as a Roman numeral
                        normalizedTier = tier.uppercase(Locale.getDefault())
                        // Check if the Roman numeral is valid and within bounds
                        if (!isValidRomanNumeral(normalizedTier)) {
                            throw Exception("There only are tiers from I to XII.")
                        }
                    }

                    // Iterate through the enum constants to find the matching tier
                    for (enumConstant in enumConstants) {
                        if (enumConstant is Enum<*>) {
                            val enumValue = enumConstant
                            // Check if the normalized tier matches the enum name
                            if (enumValue.name.equals(normalizedTier, ignoreCase = true)) {
                                return enumValue as Minions // Return the matching enum constant
                            }
                        }
                    }
                    throw Exception("No Tier with that name found.")
                } catch (e: Exception) {
                    throw Exception("Error retrieving Minion: " + e.message)
                }
            }

            throw Exception("The specified class does not implement Minions or is not an enum.")
        }

        val allMinions: MutableList<String?>
            get() {
                if (minionStringMap.isEmpty()) indexMinions()
                return ArrayList<String?>(minionStringMap.keys)
            }

        // Method to convert Arabic numeral to Roman numeral (1 to 12)
        private fun convertToRoman(number: Int): String {
            require(!(number < 1 || number > 12)) { "Number out of range for Roman numerals (1-12)." }

            when (number) {
                1 -> return "I"
                2 -> return "II"
                3 -> return "III"
                4 -> return "IV"
                5 -> return "V"
                6 -> return "VI"
                7 -> return "VII"
                8 -> return "VIII"
                9 -> return "IX"
                10 -> return "X"
                11 -> return "XI"
                12 -> return "XII"
                else -> return "" // This should never be reached due to the range check
            }
        }

        // Method to check if a Roman numeral is valid and within bounds (I to XII)
        private fun isValidRomanNumeral(roman: String): Boolean {
            when (roman) {
                "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII" -> return true
                else -> return false
            }
        }

        val minionStringMap: MutableMap<String?, Class<out Minions?>?> = HashMap<String?, Class<out Minions?>?>()
    }

    fun getTier(): Int
    fun getItems(): MutableMap<MinionResourceItem, Double>
}
