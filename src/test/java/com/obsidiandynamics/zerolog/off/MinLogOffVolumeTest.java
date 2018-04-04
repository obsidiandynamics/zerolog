package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.*;

import com.esotericsoftware.minlog.*;
import com.obsidiandynamics.testmark.*;

public final class MinLogOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void testBenchmark() {
    final String name = "MinLog";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, cycle()));
  }

  private static TestCycle cycle() {
    Log.set(Log.LEVEL_DEBUG);
    assertFalse(Log.TRACE);
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      Log.trace(String.format("float: %f, double: %f, int: %d, long: %d", f, d, i, l));
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(MinLogOffVolumeTest.class);
  }
}
