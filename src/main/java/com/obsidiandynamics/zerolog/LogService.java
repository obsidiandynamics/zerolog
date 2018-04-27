package com.obsidiandynamics.zerolog;

@FunctionalInterface
public interface LogService {
  static LogService nop() { return __name -> LogTarget.nop(); }
  
  LogTarget get(String name);
}
