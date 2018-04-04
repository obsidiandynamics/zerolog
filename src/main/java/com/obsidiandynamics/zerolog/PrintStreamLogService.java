package com.obsidiandynamics.zerolog;

import java.io.*;

public class PrintStreamLogService implements LogService {
  private final PrintStream stream;
  
  public PrintStreamLogService(PrintStream stream) {
    this.stream = stream;
  }

  @Override
  public LogTarget get(String name) {
    return new PrintStreamLogTarget(stream);
  }
}
