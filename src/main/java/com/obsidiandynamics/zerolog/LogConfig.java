package com.obsidiandynamics.zerolog;

import java.util.*;

public final class LogConfig implements ConfigService {
  private static final LogLevel defaultBaseLevel = LogLevel.CONF;
  private static LogService defaultLogService;
  
  static {
    // load any bindings on the classpath; set defaultLogService to the one with the highest priority
    final ServiceLoader<LogServiceBinding> serviceLoader = ServiceLoader.load(LogServiceBinding.class);
    final List<LogServiceBinding> prioritisedBindings = new ArrayList<>();
    serviceLoader.forEach(prioritisedBindings::add);
    Collections.sort(prioritisedBindings, LogServiceBinding::byPriorityDecreasing);
    defaultLogService = prioritisedBindings.get(0).getLogService();
  }
  
  public static LogLevel getDefaultBaseLevel() { return defaultBaseLevel; }
  
  public static LogService getDefaultLogService() { return defaultLogService; }
  
  private LogLevel baseLevel = defaultBaseLevel;
  
  private LogService logService = defaultLogService;

  LogLevel getBaseLevel() {
    return baseLevel;
  }

  public LogConfig withBaseLevel(LogLevel baseLevel) {
    this.baseLevel = baseLevel;
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
    return LogConfig.class.getSimpleName() + " [baseLevel=" + baseLevel 
        + ", logService=" + logService + "]";
  }

  @Override
  public LogConfig get() {
    return this;
  }
}
