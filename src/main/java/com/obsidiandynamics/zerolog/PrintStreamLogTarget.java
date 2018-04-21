package com.obsidiandynamics.zerolog;

import java.io.*;
import java.text.*;
import java.util.*;

import com.obsidiandynamics.zerolog.util.*;

final class PrintStreamLogTarget implements LogTarget {
  private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
  private final PrintStream stream;
  
  PrintStreamLogTarget(PrintStream stream) {
    this.stream = stream;
  }

  @Override
  public boolean isEnabled(int level) {
    return true;
  }

  @Override
  public void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable) {
    final String line;
    final String levelName = LogLevel.Enum.match(level).getShortName();
    final String threadName = Thread.currentThread().getName();
    final String time = dateFormat.format(new Date());
    final String message = SafeFormat.format(format, argc, argv);
    if (tag != null) {
      line = time + " " + levelName + " [" + threadName + "] [" + tag + "]: " + message;
    } else {
      line = time + " " + levelName + " [" + threadName + "]: " + message;
    }
    stream.println(line);
    if (throwable != null) {
      throwable.printStackTrace(stream);
    }
  }
}
