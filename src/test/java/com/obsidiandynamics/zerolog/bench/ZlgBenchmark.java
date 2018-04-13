package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import com.obsidiandynamics.zerolog.*;

public final class ZlgBenchmark extends AbstractBenchmark {
  private Zlg zlg;
  
  @Override
  public void setup() {
    zlg = Zlg.forClass(ZlgBenchmark.class)
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.CONF).get())
        .get();
    assertFalse(zlg.isEnabled(LogLevel.TRACE));
    assertTrue(zlg.t("msg").getClass().getSimpleName().equals("NopLogChain"));
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    zlg.t("float: %f, double: %f, int: %d, long: %d").arg(f).arg(d).arg(i).arg(l).done();
  }
  
  public static void main(String[] args) {
    run(ZlgBenchmark.class);
  }
}
