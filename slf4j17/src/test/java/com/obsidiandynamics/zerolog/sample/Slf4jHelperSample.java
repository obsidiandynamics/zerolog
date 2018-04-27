package com.obsidiandynamics.zerolog.sample;

import java.io.*;

import com.obsidiandynamics.zerolog.*;

public final class Slf4jHelperSample {
  private static final Zlg zlg = Zlg.forDeclaringClass()
      .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE)).get();
  
  // class nesting lets us demarcate the entrypoint
  private static class LogHelper {
    static void traceIOError(String summary, IOException cause) throws IOException {
      // override the call site entry point to the helper class
      zlg.t("I/O error: %s", z -> z.arg(summary).threw(cause).tag("I/O").entrypoint(LogHelper.class));
      throw cause;
    }
  }
  
  public static void main(String[] args) throws IOException {
    // log an error via our helper
    LogHelper.traceIOError("No more data", new EOFException());
  }
}
