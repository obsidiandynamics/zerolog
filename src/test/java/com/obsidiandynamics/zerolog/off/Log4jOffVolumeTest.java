package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import org.apache.log4j.*;
import org.junit.*;
import org.junit.runner.*;

import com.obsidiandynamics.testmark.*;

public final class Log4jOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void testBenchmark() {
    final String name = "Log4j";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, cycle()));
  }
  
  private static TestCycle cycle() {
    final Logger logger = Logger.getLogger(AbstractOffVolumeTest.class);
    assertFalse(logger.isTraceEnabled());
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      LogMF.trace(logger, "float: {}, double: {}, int: {}, long: {}", f, d, i, l);
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(Log4jOffVolumeTest.class);
  }
}
