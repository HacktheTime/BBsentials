package de.hype.bingonet.shared.constants;

import java.awt.*;

public enum StatusConstants implements BBDisplayNameProvider {
    DONEGOOD("Done", Color.GREEN),
    DONEBAD("Done", Color.ORANGE),
    WAITING("Waiting", Color.GREEN),
    FULL("Full", Color.YELLOW),
    ONGOING("Ongoing", Color.YELLOW),
    OPEN("Open", Color.GREEN),
    SPLASHING("Splashing", Color.YELLOW),
    CLOSING("Closing", Color.ORANGE),
    CLOSINGSOON("Closing Soon", Color.ORANGE),
    LEAVINGSOON("Leaving Soon", Color.ORANGE),
    CANCELED("Canceled", Color.RED),
    CLOSED("Closed", Color.RED),
    LEFT("Left", Color.ORANGE),
    ;
    String displayName;
    Color color;

    StatusConstants(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String toEnumString() {
        return super.toString();
    }


    public static StatusConstants getSplashStatus(String string) {
        for (StatusConstants value : StatusConstants.values()) {
            if (value == DONEGOOD || value == ONGOING || value == OPEN || value == CLOSING || value == CLOSINGSOON || value == LEAVINGSOON || value == CLOSED || value == LEFT) {
                continue;
            }
            if (value.toEnumString().equalsIgnoreCase(string)) {
                return value;
            }
        }
        return null;
    }
}
