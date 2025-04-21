package de.hype.bingonet.shared.constants;

/**
 * chchest Items. used to create custom ones which aren't in the {@link ChChestItems default list}
 */
public class ChChestItem {
    private String displayName;
    private String displayPath;
    private boolean custom;

    public ChChestItem(String displayName, String displayPath) {
        this.displayName = displayName;
        this.displayPath = displayPath;
        this.custom = false;
    }

    public ChChestItem(String displayName, boolean custom) {
        this.displayName = displayName;
        this.custom = custom;
        displayPath = null;
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

    public String getDisplayPath() {
        return displayPath;
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

    public boolean hasDisplayPath() {
        return displayPath != null;
    }

    public boolean isPowder() {
        return displayName.matches(".*Powder");
    }
}