package com.obsidiandynamics.zerolog;

import org.slf4j.*;

public final class Slf4jLogService implements LogService {
  @Override
  public LogTarget get(String name) {
    return new Slf4jLogTarget(LoggerFactory.getLogger(name));
  }
}
