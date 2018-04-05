package com.obsidiandynamics.zerolog.bench;

import org.openjdk.jmh.annotations.*;

import com.obsidiandynamics.dyno.*;
import com.obsidiandynamics.zerolog.*;

import squidpony.squidmath.*;

public abstract class AbstractBenchmark implements BenchmarkTarget {
  private final RandomnessSource random = new XoRoRNG();

  @Override
  public final void cycle(Abyss abyss) {
    final long randomLong = random.nextLong();
    final double randomDouble = RandomFP.toDouble(randomLong);
    final float randomFloat = (float) randomDouble;
    final int randomInt = (int) (Integer.MAX_VALUE * randomFloat);
    abyss.consume(randomInt);
    cycle(randomFloat, randomDouble, randomInt, randomLong);
  }
  
  protected abstract void cycle(float f, double d, int i, long l);
  
  static final BenchmarkResult run(Class<? extends AbstractBenchmark> target) {
    return new Dyno()
        .withBenchTime(30_000)
        .withTarget(target)
        .withDriver(new JmhDriver(opts -> opts
                                  .mode(Mode.AverageTime)
                                  .measurementIterations(3)))
        .withWarmupFraction(0.25)
        .withOutput(AbstractBenchmark::printResult)
        .run();
  }
  
  private static void printResult(BenchmarkResult result) {
    System.out.format("Average call time: %,.3f ns\n", toNanos(result));
  }
  
  static final double toNanos(BenchmarkResult result) {
    return result.getPrimaryScore() * 1_000_000_000d;
  }
}
