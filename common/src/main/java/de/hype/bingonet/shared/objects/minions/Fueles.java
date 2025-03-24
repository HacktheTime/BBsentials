package de.hype.bingonet.shared.objects.minions;

public enum Fueles {
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
    final Integer boostMultiplier;
    final Integer boostPercentage;
    final boolean bingoObtainable;
    final Integer durationMinutes;

    Fueles(Integer boostPercentage, Integer boostMultiplier, Integer durationMinutes) {
        this(boostMultiplier, boostPercentage, durationMinutes, false);
    }

    Fueles(Integer boostMultiplier, Integer boostPercentage, Integer durationMinutes, boolean bingoObtainable) {
        this.boostMultiplier = boostMultiplier;
        this.boostPercentage = boostPercentage;
        this.bingoObtainable = bingoObtainable;
        this.durationMinutes = durationMinutes;
    }

    public Integer getBoostMultiplier() {
        return Math.max(boostMultiplier, 1);
    }

    public Integer getBoostPercentage() {
        return boostPercentage;
    }

    public boolean isBingoObtainable() {
        return bingoObtainable;
    }
}

