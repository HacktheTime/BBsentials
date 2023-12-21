package de.hype.bbsentials.shared.constants;

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

    @Deprecated
    @Override
    public String toString() {
        return displayName;
    }
}
