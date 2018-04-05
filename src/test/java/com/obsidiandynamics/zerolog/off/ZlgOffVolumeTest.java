package com.obsidiandynamics.zerolog.off;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.*;

import com.obsidiandynamics.testmark.*;
import com.obsidiandynamics.zerolog.*;

public final class ZlgOffVolumeTest extends AbstractOffVolumeTest {
  @Test
  public void test() {
    runBenchmark(null, TEST_RUN_TIME_NANOS, cycle());
  }
  
  @Test
  public void testBenchmark() {
    final String name = "Zlg";
    Testmark.ifEnabled(name, () -> runBenchmark(name, BENCHMARK_RUN_TIME_NANOS, cycle()));
  }

  private static TestCycle cycle() {
    final Zlg zlg = Zlg.forClass(AbstractOffVolumeTest.class)
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.CONF).get())
        .get();
    assertFalse(zlg.isEnabled(LogLevel.TRACE));
    assertTrue(zlg.t("msg").getClass().getSimpleName().equals("NopLevelChain"));
    
    return (f, d, i, l) -> {
      consumeArgs(f, d, i, l);
      zlg.t("float: %f, double: %f, int: %d, long: %d").arg(f).arg(d).arg(i).arg(l).log();
    };
  }
  
  public static void main(String[] args) {
    Testmark.enable();
    JUnitCore.runClasses(ZlgOffVolumeTest.class);
  }
}
