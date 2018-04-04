package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.*;
import org.slf4j.*;

import com.obsidiandynamics.testmark.*;

public final class Slf4jOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void testBenchmark() {
    final String name = "SLF4J";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, cycle()));
  }
  
  private static TestCycle cycle() {
    final Logger logger = LoggerFactory.getLogger(AbstractOffVolumeTest.class);
    assertFalse(logger.isTraceEnabled());
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(Slf4jOffVolumeTest.class);
  }
}
