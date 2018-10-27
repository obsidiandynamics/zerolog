package com.obsidiandynamics.zerolog;

import org.slf4j.*;
import org.slf4j.spi.*;

import com.obsidiandynamics.format.*;

final class Slf4jLogTarget implements LogTarget {
  @FunctionalInterface
  private interface LogEnabled {
    boolean isEnabled();
  }
  
  @FunctionalInterface
  private interface LogMessage {
    void log(String message);
  }

  @FunctionalInterface
  private interface LogMarkerMessage {
    void log(Marker marker, String message);
  }

  @FunctionalInterface
  private interface LogMessageThrowable {
    void log(String message, Throwable throwable);
  }

  @FunctionalInterface
  private interface LogMarkerMessageThrowable {
    void log(Marker marker, String message, Throwable throwable);
  }
  
  static final class LogMapping {
    private final int intLevel;
    private final LogEnabled logEnabled;
    private final LogMessage logMessage;
    private final LogMarkerMessage logMarkerMessage;
    private final LogMessageThrowable logMessageThrowable;
    private final LogMarkerMessageThrowable logMarkerMessageThrowable;
    
    private LogMapping(int intLevel, LogEnabled logEnabled, LogMessage logMessage, LogMarkerMessage logMarkerMessage,
                       LogMessageThrowable logMessageThrowable, LogMarkerMessageThrowable logMarkerMessageThrowable) {
      this.intLevel = intLevel;
      this.logEnabled = logEnabled;
      this.logMessage = logMessage;
      this.logMarkerMessage = logMarkerMessage;
      this.logMessageThrowable = logMessageThrowable;
      this.logMarkerMessageThrowable = logMarkerMessageThrowable;
    }
    
    static LogMapping forLevel(Logger log, int level) {
      switch (level) {
        case LogLevel.ERROR:
          return new LogMapping(LocationAwareLogger.ERROR_INT,
                                log::isErrorEnabled,
                                log::error,
                                log::error,
                                log::error,
                                log::error);
          
        case LogLevel.WARN:
          return new LogMapping(LocationAwareLogger.WARN_INT,
                                log::isWarnEnabled,
                                log::warn,
                                log::warn,
                                log::warn,
                                log::warn);
          
        case LogLevel.INFO:
        case LogLevel.CONF:
          return new LogMapping(LocationAwareLogger.INFO_INT,
                                log::isInfoEnabled,
                                log::info,
                                log::info,
                                log::info,
                                log::info);
          
        case LogLevel.DEBUG:
          return new LogMapping(LocationAwareLogger.DEBUG_INT,
                                log::isDebugEnabled,
                                log::debug,
                                log::debug,
                                log::debug,
                                log::debug);
          
        case LogLevel.TRACE:
          return new LogMapping(LocationAwareLogger.TRACE_INT,
                                log::isTraceEnabled,
                                log::trace,
                                log::trace,
                                log::trace,
                                log::trace);
        
        case LogLevel.OFF:
        default:
          throw new UnsupportedOperationException("Unsupported level " + level);
      }
    }
  }
  
  private final boolean isLocationAware;
  
  private final Logger log;
  
  private final LogMapping mappings[] = new LogMapping[LogLevel.Enum.values().length];

  Slf4jLogTarget(Logger log) {
    this.log = log;
    isLocationAware = log instanceof LocationAwareLogger;
    
    for (LogLevel.Enum level : LogLevel.Enum.values()) {
      if (level.getLevel() != LogLevel.OFF) {
        mappings[level.ordinal()] = LogMapping.forLevel(log, level.getLevel());
      }
    }
  }
  
  private LogMapping map(int level) {
    return LogLevel.map(level, mappings);
  }

  @Override
  public boolean isEnabled(int level) {
    return map(level).logEnabled.isEnabled();
  }
  
  private static final Object[] noArgs = {};

  @Override
  public void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, String entrypoint) {
    if (isLocationAware) {
      logWithLocation(level, tag, format, argc, argv, throwable, entrypoint);
    } else {
      logDirect(level, tag, format, argc, argv, throwable);
    }
  }
  
  private void logDirect(int level, String tag, String format, int argc, Object[] argv, Throwable throwable) {
    final LogMapping mapping = map(level);
    final String message = SafeFormat.format(format, argc, argv);
    if (tag != null && throwable != null) {
      final Marker marker = MarkerFactory.getMarker(tag);
      mapping.logMarkerMessageThrowable.log(marker, message, throwable);
    } else if (tag != null) {
      final Marker marker = MarkerFactory.getMarker(tag);
      mapping.logMarkerMessage.log(marker, message);
    } else if (throwable != null) {
      mapping.logMessageThrowable.log(message, throwable);
    } else {
      mapping.logMessage.log(message);
    }
  }

  private void logWithLocation(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, 
                               String entrypoint) {
    final Marker marker = tag != null ? MarkerFactory.getMarker(tag) : null;
    final String message = SafeFormat.format(format, argc, argv);
    final int intLevel = map(level).intLevel;
    ((LocationAwareLogger) log).log(marker, entrypoint, intLevel, message, noArgs, throwable);
  }
}
