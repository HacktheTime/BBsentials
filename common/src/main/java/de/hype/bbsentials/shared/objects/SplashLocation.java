package de.hype.bbsentials.shared.objects;

public class SplashLocation {
    public final Position coords;
    public final String name;

    public SplashLocation(Position coords, String name) {
        this.coords = coords;
        this.name = name;
    }

    public SplashLocation(String name, int x, int y, int z) {
        this.name = name;
        coords = new Position(x, y, z);
    }

    public String getName() {
        if (name == null) return coords.toString();
        return name;
    }

    public boolean hasName() {
        return !(name == null || name.isEmpty());
    }

    public SplashLocation getSplashLocation() {
        return this;
    }

    public Position getCoords() {
        return coords;
    }

    public String getDisplayString() {
        if (name.equalsIgnoreCase("bea")) return "bea";
        return "%s (%d %d %d)".formatted(name, coords.x, coords.y, coords.z);
    }
}

