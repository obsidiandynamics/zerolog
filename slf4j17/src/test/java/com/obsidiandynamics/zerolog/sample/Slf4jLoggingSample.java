package com.obsidiandynamics.zerolog.sample;

import java.util.*;
import java.util.function.*;

import com.obsidiandynamics.zerolog.*;

public final class Slf4jLoggingSample {
  private static final Zlg zlg = Zlg.forDeclaringClass().get();
  
  public static void main(String[] args) {
    zlg.i("Starting with %d args: %s", z -> z.arg(args.length).arg(Arrays.asList(args)));
    final Supplier<Date> now = Date::new;
    zlg.w("An error occurred at %s", z -> z.arg(now).threw(new RuntimeException()).tag("I/O"));
  }
}
