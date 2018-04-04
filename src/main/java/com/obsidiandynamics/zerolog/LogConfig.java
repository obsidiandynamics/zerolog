package com.obsidiandynamics.zerolog;

import java.util.*;

public final class LogConfig implements ConfigService {
  private static final LogLevel defaultLevel = LogLevel.CONF;
  private static LogService defaultLogService = new SysOutLogService();
  
  private static void loadBinding(LogServiceBinding binding) {
    defaultLogService = binding.getLogService();
  }
  
  static {
    // load any bindings on the classpath; set defaultLogService to any one of the present bindings
    final ServiceLoader<LogServiceBinding> serviceLoader = ServiceLoader.load(LogServiceBinding.class);
    serviceLoader.forEach(LogConfig::loadBinding);
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
