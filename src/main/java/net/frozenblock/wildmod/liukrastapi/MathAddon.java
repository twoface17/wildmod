package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.util.Util;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.UUID;

public class MathAddon {
    public static double time;
    private static final int field_29850 = 1024;
    private static final float field_29851 = 1024.0F;
    private static final long field_29852 = 61440L;
    private static final long HALF_PI_RADIANS_SINE_TABLE_INDEX = 16384L;
    private static final long field_29854 = -4611686018427387904L;
    private static final long field_29855 = Long.MIN_VALUE;
    public static final float PI = (float) Math.PI;
    public static final float HALF_PI = (float) (Math.PI / 2);
    public static final float TAU = (float) (Math.PI * 2);
    public static final float RADIANS_PER_DEGREE = (float) (Math.PI / 180.0);
    public static final float DEGREES_PER_RADIAN = 180.0F / (float)Math.PI;
    public static final float EPSILON = 1.0E-5F;
    public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
    private static final float DEGREES_TO_SINE_TABLE_INDEX = 10430.378F;
    private static final float[] SINE_TABLE = (float[]) Util.make(new float[65536], sineTable -> {
        for(int ix = 0; ix < sineTable.length; ++ix) {
            sineTable[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
        }

    });
    private static final AbstractRandom RANDOM = WildAbstractRandom.createBlocking();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
            0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    };
    private static final double field_29857 = 0.16666666666666666;
    private static final int field_29858 = 8;
    private static final int field_29859 = 257;
    private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ARCSINE_TABLE = new double[257];
    private static final double[] COSINE_TABLE = new double[257];

    public MathAddon() {
    }

    public static float sin(float value) {
        return SINE_TABLE[(int)(value * 10430.378F) & 65535];
    }

    public static float cos(float value) {
        return SINE_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
    }

    public static float sqrt(float value) {
        return (float)Math.sqrt((double)value);
    }

    public static double cutCos(double value, double offset, boolean inverse) {
        double equation = Math.cos(value);
        if(!inverse) {
            return Math.max(equation, offset);
        } else {
            return Math.max(-equation, offset);
        }
    }

    public static float pow(float value, float power) {
        return (float) Math.pow(value, power);
    }

    public static float method_41303(float f, float g, float h, float i, float j) {
        return 0.5F * (2.0F * h + (i - g) * f + (2.0F * g - 5.0F * h + 4.0F * i - j) * f * f + (3.0F * h - g - 3.0F * i + j) * f * f * f);
    }

    public static double squaredMagnitude(double d, double e, double f) {
        return d * d + e * e + f * f;
    }

    public static double cutSin(double value, double offset, boolean inverse) {
        double equation = Math.sin(value);
        if(!inverse) {
            return Math.max(equation, offset);
        } else {
            return Math.max(-equation, offset);
        }
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double distancex;
        double distancey;
        double distancez;

        if(x1 > x2) {distancex = x1 - x2;} else {distancex = x2 -x1;}
        if(y1 > y2) {distancey = y1 - y2;} else {distancey = y2 -y1;}
        if(z1 > z2) {distancez = z1 - z2;} else {distancez = z2 -z1;}

        return Math.sqrt(Math.pow(distancex, 2) + Math.pow(distancey, 2) + Math.pow(distancez, 2));
    }

    public static int nextBetween(AbstractRandom abstractRandom, int min, int max) {
        return abstractRandom.nextInt(max - min + 1) + min;
    }

    public static float nextBetween(AbstractRandom abstractRandom, float min, float max) {
        return abstractRandom.nextFloat() * (max - min) + min;
    }

    public static UUID randomUuid(WildAbstractRandom random) {
        long l = random.nextLong() & -61441L | 16384L;
        long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
        return new UUID(l, m);
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static float fastInverseSqrt(float x) {
        float f = 0.5F * x;
        int i = Float.floatToIntBits(x);
        i = 1597463007 - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5F - f * x * x);
    }

    public static double fastInverseSqrt(double x) {
        double d = 0.5 * x;
        long l = Double.doubleToRawLongBits(x);
        l = 6910469410427058090L - (l >> 1);
        x = Double.longBitsToDouble(l);
        return x * (1.5 - d * x * x);
    }

    public static float fastInverseCbrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * x);
        return 0.6666667F * f + 1.0F / (3.0F * f * f * x);
    }

    public static double atan2(double y, double x) {
        double d = x * x + y * y;
        if (Double.isNaN(d)) {
            return Double.NaN;
        } else {
            boolean bl = y < 0.0;
            if (bl) {
                y = -y;
            }

            boolean bl2 = x < 0.0;
            if (bl2) {
                x = -x;
            }

            boolean bl3 = y > x;
            if (bl3) {
                double e = x;
                x = y;
                y = e;
            }

            double e = fastInverseSqrt(d);
            x *= e;
            y *= e;
            double f = SMALLEST_FRACTION_FREE_DOUBLE + y;
            int i = (int)Double.doubleToRawLongBits(f);
            double g = ARCSINE_TABLE[i];
            double h = COSINE_TABLE[i];
            double j = f - SMALLEST_FRACTION_FREE_DOUBLE;
            double k = y * h - x * j;
            double l = (6.0 + k * k) * k * 0.16666666666666666;
            double m = g + l;
            if (bl3) {
                m = Math.PI / 2 - m;
            }

            if (bl2) {
                m = Math.PI - m;
            }

            if (bl) {
                m = -m;
            }

            return m;
        }
    }
}
