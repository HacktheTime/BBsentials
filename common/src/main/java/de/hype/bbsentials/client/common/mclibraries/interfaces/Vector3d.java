package de.hype.bbsentials.client.common.mclibraries.interfaces;

import de.hype.bbsentials.shared.objects.Position;

public class Vector3d implements Comparable<Vector3d> {

    public static final Vector3d NULL_VECTOR = new Vector3d(0, 0, 0);
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
            0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    };
    private static final int SIZE_BITS_X = 1 + floorLog2(smallestEncompassingPowerOfTwo(30000000));
    private static final int SIZE_BITS_Z = SIZE_BITS_X;
    public static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
    private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
    private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
    private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
    private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;
    private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
    private static final float[] SIN_TABLE = new float[65536];

    static {
        int i;
        for (i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }
    }

    public final double x;
    public final double y;
    public final double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3d(Position pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    private static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    public static int floorLog2(int value) {
        return ceilLog2(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int ceilLog2(int value) {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) value * 125613361L >> 27) & 31];
    }

    private static long doubleToLongBits(double d) {
        return d == 0.0 ? 0L : Double.doubleToLongBits(d);
    }

    public static float sin(float f) {
        return SIN_TABLE[(int) (f * 10430.378F) & '\uffff'];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & '\uffff'];
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public long asLong() {
        long l = 0L;
        l |= ((long) x & BITS_X) << BIT_SHIFT_X;
        l |= ((long) y & BITS_Y) << 0;
        return l | ((long) z & BITS_Z) << BIT_SHIFT_Z;
    }

    public Vector3d subtractReverse(Vector3d vec) {
        return new Vector3d(vec.x - this.x, vec.x - this.y, vec.z - this.z);
    }

    public Vector3d normalize() {
        double base = this.x * this.x + this.y * this.y + this.z * this.z;
        return base < 1.0E-4D ? new Vector3d(0.0D, 0.0D, 0.0D) : new Vector3d(this.x / base, this.y / base, this.z / base);
    }

    public Vector3d crossProduct(Vector3d vec) {
        return new Vector3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Vector3d subtract(Vector3d vec) {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public Vector3d subtract(double x, double y, double z) {
        return this.addVector(-x, -y, -z);
    }

    public Vector3d add(Vector3d vec) {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this vector.
     */
    public Vector3d addVector(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public double distanceTo(Vector3d vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public Vector3d getIntermediateWithXValue(Vector3d vec, double x) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d0 * d0 < 1.0000000116860974E-7D) {
            return null;
        }
        else {
            double d3 = (x - this.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the passed in vector, or null if not possible.
     */
    public Vector3d getIntermediateWithYValue(Vector3d vec, double y) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        }
        else {
            double d3 = (y - this.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the passed in vector, or null if not possible.
     */
    public Vector3d getIntermediateWithZValue(Vector3d vec, double z) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        }
        else {
            double d3 = (z - this.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    public Vector3d rotatePitch(float pitch) {
        float f = cos(pitch);
        float f1 = sin(pitch);
        double d0 = this.x;
        double d1 = this.y * (double) f + this.z * (double) f1;
        double d2 = this.z * (double) f - this.y * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    public Vector3d rotateYaw(float yaw) {
        float f = cos(yaw);
        float f1 = sin(yaw);
        double d0 = this.x * (double) f + this.z * (double) f1;
        double d1 = this.y;
        double d2 = this.z * (double) f - this.x * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        else if (!(other instanceof Vector3d)) {
            return false;
        }
        else {
            Vector3d Vectorc = (Vector3d) other;
            return this.x == Vectorc.x && this.y == Vectorc.y && this.z == Vectorc.z;
        }
    }

    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + doubleToLongBits(x);
        bits = 31L * bits + doubleToLongBits(y);
        bits = 31L * bits + doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }

    public int compareTo(Vector3d other) {
        return this.y == other.y ?
                (this.z == other.z ?
                        (int) (this.x - other.x)
                        : (int) (this.z - other.z))
                : (int) (this.y - other.y);
    }

    public boolean signumEquals(Vector3d other) {
        return Math.signum(x) == Math.signum(other.x) &&
                Math.signum(y) == Math.signum(other.y) &&
                Math.signum(z) == Math.signum(other.z);
    }

    public double sqrt(double value) {
        return Math.sqrt(value);
    }
}

