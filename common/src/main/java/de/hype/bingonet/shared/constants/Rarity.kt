package de.hype.bingonet.shared.constants

enum class Rarity(@JvmField val brank: Int?) {
    COMMON(0),
    UNCOMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4),
    MYTHIC(5),
    DIVINE(6),
    SPECIAL(null),
    VERY_SPECIAL(null),
    ULTIMATE(null),
    ADMIN(null);

}
