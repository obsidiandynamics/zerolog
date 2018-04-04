package com.obsidiandynamics.zerolog;

public final class Slf4jBinding implements LogServiceBinding {
  @Override
  public LogService getLogService() {
    return new Slf4jLogService();
  }
}
