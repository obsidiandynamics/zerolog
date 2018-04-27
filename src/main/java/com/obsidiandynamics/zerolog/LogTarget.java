package com.obsidiandynamics.zerolog;

public interface LogTarget {
  static LogTarget nop = new LogTarget() {
    @Override
    public boolean isEnabled(int level) { return false; }

    @Override
    public void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable,
                    String entrypoint) {}
  };
  
  static LogTarget nop() { return nop; }
  
  boolean isEnabled(int level);
  
  void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, String entrypoint);
}
