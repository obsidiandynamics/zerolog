package com.obsidiandynamics.zerolog;

import org.slf4j.*;

public final class Slf4jWrapper {
  private Slf4jWrapper() {}
  
  public static LogService of(Logger logger) {
    return __ -> new Slf4jLogTarget(logger);
  }
}
