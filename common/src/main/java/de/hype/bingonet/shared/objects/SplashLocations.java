package de.hype.bingonet.shared.objects;

public class SplashLocations {
    public static final SplashLocation BEA = new SplashLocation("bea", 26, 70, -93);
    public static final SplashLocation KAT = new SplashLocation("kat", 29, 71, -104);
    public static final SplashLocation ENCHANTING_TABLE = new SplashLocation("Enchanting Table", -33, 69, -109);
    public static final SplashLocation COMMUNITY_CENTER = new SplashLocation("Community Center", 2, 71, -99);

    public static SplashLocation[] values() {
        return new SplashLocation[]{BEA, KAT, ENCHANTING_TABLE, COMMUNITY_CENTER};
    }

    public static SplashLocation getFromExactCoords(Position position) {
        for (SplashLocation value : values()) {
            if (value.coords.equals(position)) {
                return value;
            }
        }
        return new SplashLocation(position, null);
    }
}
