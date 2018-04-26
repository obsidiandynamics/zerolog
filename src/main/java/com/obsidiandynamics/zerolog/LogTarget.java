package com.obsidiandynamics.zerolog;

public interface LogTarget {
  boolean isEnabled(int level);
  
  void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, String entrypoint);
}
