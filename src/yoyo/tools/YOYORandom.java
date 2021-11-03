// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.util.Random;

public class YOYORandom {

    private static Random rnd;

    static {
        YOYORandom.rnd = new Random();
    }

    public static void reset() {
        long cur = System.currentTimeMillis();
        YOYORandom.rnd.setSeed(cur);
    }

    public static Random newRandom() {
        return new Random();
    }

    public static boolean probility(final double probility) {
        int probInt = (int) (probility * 100.0);
        probInt = Math.abs(probInt);
        int random = nextInt(100);
        return random < probInt;
    }

    public static int getRndInt() {
        return YOYORandom.rnd.nextInt();
    }

    public static boolean nextBoolean() {
        return YOYORandom.rnd.nextBoolean();
    }

    public static int nextInt(final int range) {
        if (range <= 0) {
            return 0;
        }
        return YOYORandom.rnd.nextInt(range);
    }

    public static void nextBytes(final byte[] bytes) {
        YOYORandom.rnd.nextBytes(bytes);
    }

    public static int nextInt(int min, final int max) {
        if (min < 0) {
            min = 0;
        }
        int offset = max - min;
        return Math.abs(YOYORandom.rnd.nextInt()) % (Math.abs(offset) + 1) + min;
    }

    public static long nextLong() {
        return YOYORandom.rnd.nextLong();
    }

    public static float nextFloat() {
        return YOYORandom.rnd.nextFloat();
    }

    public static Double nextDouble() {
        return YOYORandom.rnd.nextDouble();
    }
}
