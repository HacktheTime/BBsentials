package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.ChChestItem;

import java.util.List;

public class ChChestData {
    public String finder;
    public Position coords;
    public List<ChChestItem> items;

    public ChChestData(String finder, Position coords, List<ChChestItem> items) {
        this.finder = finder;
        this.coords = coords;
        this.items = items;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChestLobbyData)) return false;
        return ((ChChestData) obj).coords.equals(coords);
    }

    @Override
    public int hashCode() {
        return coords.hashCode();
    }
}
