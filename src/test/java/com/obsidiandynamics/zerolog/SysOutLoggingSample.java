package com.obsidiandynamics.zerolog;

import java.util.*;

public final class SysOutLoggingSample {
  private static final Zlg zlg = Zlg.forClass(SysOutLoggingSample.class).get();
  
  public static void main(String[] args) {
    zlg.i("Starting with %d args: %s").arg(args.length).arg(Arrays.asList(args)).log();
    zlg.w("An error occurred at %s").arg(new Date()).stack(new RuntimeException()).tag("I/O").log();
  }
}
