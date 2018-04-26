package com.obsidiandynamics.zerolog;

@FunctionalInterface
public interface LogService {
  static LogService nop() { return __name -> null; }
  
  LogTarget get(String name);
}
