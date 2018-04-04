package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.*;
import org.pmw.tinylog.*;
import org.pmw.tinylog.writers.*;

import com.obsidiandynamics.testmark.*;

public final class TinyLogOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void testBenchmark() {
    final String name = "TinyLog";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, cycle()));
  }

  private static TestCycle cycle() {
    Configurator.defaultConfig()
    .writer(new ConsoleWriter())
    .level(Level.INFO)
    .activate();
    assertEquals(Level.INFO, Logger.getLevel());
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      Logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(TinyLogOffVolumeTest.class);
  }
}
