package com.obsidiandynamics.zerolog.off;

import org.junit.*;
import org.junit.runners.*;

import squidpony.squidmath.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractOffVolumeTest {
  protected static final long TEST_RUN_TIME_NANOS = 10_000_000L;
  protected static final long BENCHMARK_RUN_TIME_NANOS = 30_000_000_000L;
  
  @FunctionalInterface
  protected interface TestCycle {
    void cycle(float randomFloat, double randomDouble, int randomInt, long randomLong);
  }
  
  protected static final void runBenchmark(String name, long runTimeNanos, TestCycle cycle) {
    final long warmupTime = runTimeNanos / 4;
    final long checkRuns = 1_000_000L;
    boolean warmup = true;
    
    long cycles = 0;
    long took;
    final RandomnessSource random = new XoRoRNG();
    if (name != null) System.out.print("Warming up... ");
    long started = System.nanoTime();
    for (;;) {
      // Generate random numbers to avoid cache hits for boxed primitives and any JIT optimisations.
      // Each primitive is assigned from another in a cascade. The last primitive is asserted by each
      // of the target sinks to avoid JIT dead code elimination.
      final long randomLong = random.nextLong();
      final double randomDouble = RandomFP.toDouble(randomLong);
      final float randomFloat = (float) randomDouble;
      final int randomInt = (int) (Integer.MAX_VALUE * randomFloat);
      
      cycle.cycle(randomFloat, randomDouble, randomInt, randomLong);
      cycles++;
      if (cycles % checkRuns == 0) {
        took = System.nanoTime() - started;
        if (warmup) {
          if (took > warmupTime) {
            warmup = false;
            if (name != null) System.out.println("starting timed run... ");
            cycles = 0;
            started = System.nanoTime();
          }
        } else {
          if (took > runTimeNanos) {
            break;
          }
        }
      }
    }
    
    if (name != null) {
      final double perCycle = (double) took / cycles;
      final double tookMillis = took / 1_000_000d;
      System.out.format("%-10s: %,d cycles took %,.0f ms, %,.2f ns/cycle\n", name, cycles, tookMillis, perCycle);
    }
  }
  
  protected static final void consumeArgs(float randomFloat, double randomDouble, int randomInt, long randomLong) {
    // randomInt is the last primitive in the assignment chain, so testing it guarantees that all prior
    // operations have been carried out
    if (randomInt < 0) throw new AssertionError();
  }
}
