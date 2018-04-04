package com.obsidiandynamics.zerolog;

public final class SysOutLogService extends PrintStreamLogService {
  public SysOutLogService() {
    super(System.out);
  }
}
