package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.ChChestItem;

public class ChChestData {
    public String finder;
    public int coordx;
    public int coordy;
    public int coordz;
    public ChChestItem[] items;

    public ChChestData(String finder, String locationCoords, ChChestItem[] items) {
        this.finder = finder;
        String[] tempCoords = locationCoords.split(" ");
        this.coordx = Integer.parseInt(tempCoords[0]);
        this.coordy = Integer.parseInt(tempCoords[1]);
        this.coordz = Integer.parseInt(tempCoords[2]);
        this.items = items;
    }

    public String getCoords() {
        return coordx + " " + coordy + " " + coordz;
    }
}
