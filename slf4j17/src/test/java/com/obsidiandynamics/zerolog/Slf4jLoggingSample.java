package com.obsidiandynamics.zerolog;

import java.util.*;

public final class Slf4jLoggingSample {
  private static final Zlg zlg = Zlg
      .forClass(Slf4jLoggingSample.class)
      .withConfigService(new LogConfig().withRootLevel(LogLevel.TRACE).withLogService(new Slf4jLogService()))
      .get();
  
  public static void main(String[] args) {
    zlg.i("Starting with %d args: %s").arg(args.length).arg(Arrays.asList(args)).log();
    zlg.d("An error occurred at %s").arg(new Date()).stack(new RuntimeException()).tag("I/O").log();
  }
}
