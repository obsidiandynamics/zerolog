package com.obsidiandynamics.zerolog.sample;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

import com.obsidiandynamics.zerolog.*;

public final class SysOutLoggingSample {
  private static final Zlg zlg = Zlg
      .forClass(MethodHandles.lookup().lookupClass())
      .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE))
      .get();
  
  public static void open(String address, int port, double timeoutSeconds) {
    final List<Integer> numbers = Arrays.asList(5, 6, 7, 8);
    zlg.i("The list %s has %d elements", z -> z.arg(numbers).arg(numbers::size).tag("list"));
    
    zlg.i("Pi is %.2f", z -> z.arg(Math.PI));
    zlg.i("Connecting to %s:%d [timeout: %.1f sec]", z -> z.arg(address).arg(port).arg(timeoutSeconds));
    
    try {
      openSocket(address, port, timeoutSeconds);
    } catch (IOException e) {
      zlg.w("Error connecting to %s:%d", z -> z.arg(address).arg(port).tag("I/O").threw(e));
    }
  }
  
  private static void openSocket(String address, int port, double timeoutSeconds) throws IOException {
    throw new IOException("Connection timed out");
  }
  
  public static void main(String[] args) {
    open("github.com", 80, 30);
  }
}
