package com.obsidiandynamics.zerolog;

import java.lang.invoke.*;

public final class ZeroOverheadLoggingSample {
  private static final Zlg zlg = Zlg.forClass(MethodHandles.lookup().lookupClass()).get();
  
  private static final boolean TRACE_ENABLED = false;
  
  public static void withStaticConstant(String address, int port, double timeoutSeconds) {
    if (TRACE_ENABLED) zlg.t("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeoutSeconds).log();
  }
  
  public static void withAssert(String address, int port, double timeoutSeconds) {
    assert zlg.t("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeoutSeconds).logb();
  }
  
  public static void main(String[] args) {
    withStaticConstant("github.com", 80, 30);
    withAssert("github.com", 80, 30);
  }
}
