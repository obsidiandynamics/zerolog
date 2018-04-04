package com.obsidiandynamics.zerolog;

@FunctionalInterface
public interface LogService {
  LogTarget get(String name);
}
