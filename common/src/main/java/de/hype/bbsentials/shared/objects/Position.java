package de.hype.bbsentials.shared.objects;

public class Position extends Vector3i {


    public static final Position ORIGIN = new Position(0, 0, 0);

    public Position(int x, int y, int z) {
        super(x, y, z);
    }

    public Position(Vector3i vector) {
        super(vector.x, vector.y, vector.z);
    }

    /**
     * @param string nneds to be `x y z` formating!
     */
    public static Position fromString(String string) throws Exception {
        String[] temp = string.split(" ");
        return new Position(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public String toFullString() {
        return "X:" + x + " Y:" + y + " Z:" + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) return false;
        Position pos2 = ((Position) obj);
        return (pos2.x == x && pos2.y == y && pos2.z == z);
    }

    public Double getDistanceBetween(Position pos) {
        int x = pos.x - this.x;
        int y = pos.y - this.y;
        int z = pos.z - this.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public boolean isInRange(Position otherPos, int range) {
        return getDistanceBetween(otherPos) < range;
    }
}
