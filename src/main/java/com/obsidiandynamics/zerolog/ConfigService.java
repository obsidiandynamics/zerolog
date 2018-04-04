package com.obsidiandynamics.zerolog;

@FunctionalInterface
public interface ConfigService {
  LogConfig get();
}
