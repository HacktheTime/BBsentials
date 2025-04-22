package de.hype.bingonet.shared.objects

import de.hype.bingonet.shared.constants.ChChestItem

open class ChChestData(
    @JvmField var finder: String,
    @JvmField var coords: Position,
    @JvmField var items: MutableList<ChChestItem>
) {
    override fun equals(obj: Any?): Boolean {
        if (obj !is ChChestData) return false
        return obj.coords == coords
    }

    override fun hashCode(): Int {
        return coords.hashCode()
    }
}
