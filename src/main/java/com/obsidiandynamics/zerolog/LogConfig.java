package com.obsidiandynamics.zerolog;

import java.util.*;

public final class LogConfig implements ConfigService {
  private static final int DEFAULT_BASE_LEVEL = LogLevel.CONF;
  private static final LogService defaultLogService;
  
  static {
    // load any bindings on the classpath; set defaultLogService to the one with the highest priority
    final ServiceLoader<LogServiceBinding> serviceLoader = ServiceLoader.load(LogServiceBinding.class);
    final List<LogServiceBinding> prioritisedBindings = new ArrayList<>();
    serviceLoader.forEach(prioritisedBindings::add);
    prioritisedBindings.sort(LogServiceBinding::byPriorityDecreasing);
    defaultLogService = prioritisedBindings.get(0).getLogService();
  }
  
  private int baseLevel = DEFAULT_BASE_LEVEL;
  
  private LogService logService = defaultLogService;

  int getBaseLevel() {
    return baseLevel;
  }

  public LogConfig withBaseLevel(int baseLevel) {
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
