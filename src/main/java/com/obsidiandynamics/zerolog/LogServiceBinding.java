package com.obsidiandynamics.zerolog;

@FunctionalInterface
public interface LogServiceBinding {
  LogService getLogService();
}
