package de.hype.bbsentials.shared.objects.minions;

public enum MinionStorage {
    SMALL("Small Storage", 3),
    MEDIUM("Medium Storage", 9),
    LARGE("Large Storage", 15),
    XLARGE("X-Large Storage", 21),
    XXLARGE("XX-Large Storage", 27);

    private final Integer storage;
    private final Integer storageSlots;
    private final String displayName;

    MinionStorage(String displayName, int slots) {
        this.displayName = displayName;
        this.storageSlots = slots;
        this.storage = slots * 64;
    }

    public Integer getStorage() {
        return storage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getStorageSlots() {
        return storageSlots;
    }
}
