package com.obsidiandynamics.zerolog.sample;

import java.lang.invoke.*;

import com.obsidiandynamics.zerolog.*;

public final class ZeroOverheadLoggingSample {
  private static final Zlg zlg = Zlg.forClass(MethodHandles.lookup().lookupClass()).get();
  
  private static final boolean TRACE_ENABLED = false;
  
  public static void withStaticConstant(String address, int port, double timeout) {
    if (TRACE_ENABLED) {
      zlg.t("Connecting to %s:%d [timeout: %.1f sec]", z -> z.arg(address).arg(port).arg(timeout));
    }
  }
  
  public static void withAssert(String address, int port, double timeout) {
    assert zlg.level(LogLevel.TRACE).format("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeout).log();
  }
  
  public static void main(String[] args) {
    withStaticConstant("github.com", 80, 30);
    withAssert("github.com", 80, 30);
  }
}
