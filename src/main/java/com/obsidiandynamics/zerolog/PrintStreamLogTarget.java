package com.obsidiandynamics.zerolog;

import java.io.*;
import java.text.*;
import java.util.*;

final class PrintStreamLogTarget implements LogTarget {
  private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
  private final PrintStream stream;
  
  PrintStreamLogTarget(PrintStream stream) {
    this.stream = stream;
  }

  @Override
  public boolean isEnabled(LogLevel level) {
    return true;
  }

  @Override
  public void log(LogLevel level, String tag, String format, int argc, Object[] argv, Throwable throwable) {
    final String line;
    final String levelName = level.getShortName();
    final String threadName = Thread.currentThread().getName();
    final String time = dateFormat.format(new Date());
    final String message = String.format(format, argv);
    if (tag != null) {
      line = time + " " + levelName + " [" + threadName + "] <" + tag + ">: " + message;
    } else {
      line = time + " " + levelName + " [" + threadName + "]: " + message;
    }
    stream.println(line);
    if (throwable != null) {
      throwable.printStackTrace(stream);
    }
  }
}
