package com.obsidiandynamics.zerolog;

public final class SysOutBinding implements LogServiceBinding {
  @Override
  public byte getPriority() {
    return 0;
  }
  
  @Override
  public LogService getLogService() {
    return new SysOutLogService();
  }
}
