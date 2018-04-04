package com.obsidiandynamics.zerolog;

public interface LogServiceBinding {
  byte getPriority();
  
  LogService getLogService();
  
  static int byPriorityDecreasing(LogServiceBinding b0, LogServiceBinding b1) {
    return Byte.compare(b1.getPriority(), b0.getPriority());
  }
}
