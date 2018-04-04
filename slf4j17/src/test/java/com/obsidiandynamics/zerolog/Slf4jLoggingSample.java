package com.obsidiandynamics.zerolog;

import java.lang.invoke.*;
import java.util.*;

public final class Slf4jLoggingSample {
  private static final Zlg zlg = Zlg.forClass(MethodHandles.lookup().lookupClass()).get();
  
  public static void main(String[] args) {
    zlg.i("Starting with %d args: %s").arg(args.length).arg(Arrays.asList(args)).log();
    zlg.w("An error occurred at %s").arg(new Date()).stack(new RuntimeException()).tag("I/O").log();
  }
}
