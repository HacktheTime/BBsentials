package de.hype.bbsentials.shared.objects;

public class Position {
    public int x;
    public int y;
    public int z;


    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @param string nneds to be `x y z` formating!
     */
    public Position(String string) throws Exception {
        String[] temp = string.split(" ");
        x = Integer.parseInt(temp[0]);
        y = Integer.parseInt(temp[1]);
        z = Integer.parseInt(temp[2]);
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
        Integer x = pos.x - this.x;
        Integer y = pos.y - this.y;
        Integer z = pos.z - this.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public boolean isInRange(Position otherPos, int range) {
        return getDistanceBetween(otherPos) < range;
    }
}
