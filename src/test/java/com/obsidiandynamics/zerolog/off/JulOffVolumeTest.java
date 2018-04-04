package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import java.util.logging.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import com.obsidiandynamics.testmark.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class JulOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void test1_lambdaBenchmark() {
    final String name = "JUL-lambda";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, lambdaCycle()));
  }
  
  @Test
  public void test2_arrayBenchmark() {
    final String name = "JUL-array";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, arrayCycle()));
  }

  private static TestCycle arrayCycle() {
    final Logger logger = Logger.getLogger(JulOffVolumeTest.class.getName());
    logger.setLevel(Level.INFO);
    assertFalse(logger.isLoggable(Level.FINEST));
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      logger.log(Level.FINEST, "float: {0}, double: {1}, int: {2}, long: {3}", new Object[] {f, d, i, l});
    };
  }

  private static TestCycle lambdaCycle() {
    final Logger logger = Logger.getLogger(JulOffVolumeTest.class.getName());
    logger.setLevel(Level.INFO);
    assertFalse(logger.isLoggable(Level.FINEST));
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      logger.log(Level.FINEST, () -> String.format("float: %f, double: %f, int: %d, long: %d", f, d, i, l));
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(JulOffVolumeTest.class);
  }
}
