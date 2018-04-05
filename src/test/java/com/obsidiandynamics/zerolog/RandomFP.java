package com.obsidiandynamics.zerolog;

/**
 *  Taken from {@code SplittableRandom}.
 */
public final class RandomFP {
  private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53);

  private RandomFP() {}

  public static double toDouble(long z) {
    return (mix64(z) >>> 11) * DOUBLE_UNIT;
  }

  private static long mix64(long z) {
    z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
    z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
    return z ^ (z >>> 31);
  }
}
