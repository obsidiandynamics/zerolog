package com.obsidiandynamics.zerolog;

import java.io.*;
import java.lang.invoke.*;

public final class SysOutLoggingSample {
  private static final Zlg zlg = Zlg
      .forClass(MethodHandles.lookup().lookupClass())
      .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE))
      .get();
  
  public static void open(String address, int port, double timeoutSeconds) {
    zlg.i("Pi is %d").arg(3.14).log(); //TODO
    zlg.i("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeoutSeconds).log();
    try {
      openSocket(address, port, timeoutSeconds);
    } catch (IOException e) {
      zlg.w("Error connecting to %s:%d").arg(address).arg(port).tag("I/O").threw(e).log();
    }
  }
  
  private static void openSocket(String address, int port, double timeoutSeconds) throws IOException {
    throw new IOException("Connection timed out");
  }
  
  public static void main(String[] args) {
    open("github.com", 80, 30);
  }
}
