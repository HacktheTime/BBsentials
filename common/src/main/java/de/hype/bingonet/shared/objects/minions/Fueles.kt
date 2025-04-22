package de.hype.bingonet.shared.objects.minions

import kotlin.math.max

enum class Fueles(
    val boostMultiplier: Int,
    val boostPercentage: Int,
    val durationMinutes: Int,
    val isBingoObtainable: Boolean
) {
    COAL(5, 0, 30),
    CHARCOAL(5, 0, 30),
    BLOCK_OF_COAL(5, 0, 5 * 60),
    ENCHANTED_COAL(10, 0, 24 * 60),
    ENCHANTED_CHAR_COAL(20, 0, 36 * 60),
    HAMSTER_WHEEL(50, 0, 24 * 60),
    FOUL_FLESH(90, 0, 5 * 60),
    ENCHANTED_BREAD(5, 0, 12 * 60),
    CATALYST(0, 3, 3 * 60),
    HYPER_CATALYST(0, 4, 6 * 60, false),
    TASTY_CHESSE(0, 2, 1 * 60),
    SOLAR_PANEL(25, 0, -1, false),
    ENCHANTED_LAVA_BUCKET(25, 0, -1),
    MAGMA_BUCKET(30, 0, -1, false),
    PLASMA_BUCKET(35, 0, -1, false),
    EVER_BURNING_FLAME(35, 0, -1, false);

    constructor(boostPercentage: Int, boostMultiplier: Int, durationMinutes: Int) : this(
        boostMultiplier,
        boostPercentage,
        durationMinutes,
        false
    )

    fun getBoostMultiplier(): Int {
        return max(boostMultiplier, 1)
    }
}

