package de.hype.bingonet.shared.objects.minions

enum class MinionStorage(val displayName: String, slots: Int) {
    SMALL("Small Storage", 3),
    MEDIUM("Medium Storage", 9),
    LARGE("Large Storage", 15),
    XLARGE("X-Large Storage", 21),
    XXLARGE("XX-Large Storage", 27);

    val storage: Int
    val storageSlots: Int

    init {
        this.storageSlots = slots
        this.storage = slots * 64
    }
}
