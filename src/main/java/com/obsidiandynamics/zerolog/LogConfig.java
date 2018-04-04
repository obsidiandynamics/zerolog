package com.obsidiandynamics.zerolog;

import java.util.*;

public final class LogConfig implements ConfigService {
  private static final LogLevel defaultLevel = LogLevel.CONF;
  private static LogService defaultLogService;
  
  static {
    // load any bindings on the classpath; set defaultLogService to the one with the highest priority
    final ServiceLoader<LogServiceBinding> serviceLoader = ServiceLoader.load(LogServiceBinding.class);
    final List<LogServiceBinding> prioritisedBindings = new ArrayList<>();
    serviceLoader.forEach(prioritisedBindings::add);
    Collections.sort(prioritisedBindings, LogServiceBinding::byPriorityDecreasing);
    defaultLogService = prioritisedBindings.get(0).getLogService();
  }
  
  public static LogLevel getDefaultLevel() { return defaultLevel; }
  
  public static LogService getDefaultLogService() { return defaultLogService; }
  
  private LogLevel rootLevel = LogLevel.CONF;
  
  private LogService logService = defaultLogService;

  LogLevel getRootLevel() {
    return rootLevel;
  }

  public LogConfig withRootLevel(LogLevel rootLevel) {
    this.rootLevel = rootLevel;
    return this;
  }

  LogService getLogService() {
    return logService;
  }

  public LogConfig withLogService(LogService logService) {
    this.logService = logService;
    return this;
  }

  @Override
  public String toString() {
    return LogConfig.class.getSimpleName() + " [rootLevel=" + rootLevel 
        + ", logService=" + logService + "]";
  }

  @Override
  public LogConfig get() {
    return this;
  }
}
