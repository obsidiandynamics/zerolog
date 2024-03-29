package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import com.obsidiandynamics.zerolog.*;

public final class ZlgAssertBenchmark extends AbstractBenchmark {
  private Zlg zlg;
  
  @Override
  public void setup() {
    zlg = Zlg.forDeclaringClass()
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.CONF).get())
        .get();
    assertFalse(zlg.isEnabled(LogLevel.TRACE));
    assertEquals("NopLogChain", zlg.level(LogLevel.TRACE).format("msg").getClass().getSimpleName());
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    assert zlg.level(LogLevel.TRACE).format("float: %f, double: %f, int: %d, long: %d").arg(f).arg(d).arg(i).arg(l).log();
  }
  
  public static void main(String[] args) {
    run(ZlgAssertBenchmark.class);
  }
}
