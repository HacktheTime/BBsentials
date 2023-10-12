package de.hype.bbsentials.common.constants.enviromentShared;

public class ChChestItem {
    private String displayName;
    private boolean custom;

    public ChChestItem(String displayName) {
        this.displayName = displayName;
        this.custom = false;
    }

    public ChChestItem(String displayName, boolean custom) {
        this.displayName = displayName;
        this.custom = custom;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChChestItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public boolean isCustom() {
        return custom;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean isGemstone() {
        return displayName.startsWith("Flawless") && displayName.endsWith("Gemstone");
    }

    public boolean isRoboPart() {
        String[] roboParts = {"Control Switch", "Electron Transmitter", "FTX 3070", "Robotron Reflector", "Superlite Motor", "Synthetic Heart"};
        for (String roboPart : roboParts) {
            if (displayName.equals(roboPart)) return true;
        }
        return false;
    }
}