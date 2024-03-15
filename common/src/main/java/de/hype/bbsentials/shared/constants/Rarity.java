package de.hype.bbsentials.shared.constants;

public enum Rarity {
    COMMON(0),
    UNCOMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4),
    MYTHIC(5),
    DIVINE(6),
    SPECIAL(-1),
    VERY_SPECIAL(-1),
    ULTIMATE(-1),
    ADMIN(-1);

    public final Integer brank;

    Rarity(Integer brank) {
        this.brank = brank;
    }

    public Integer getBrank() {
        return brank;
    }
}
