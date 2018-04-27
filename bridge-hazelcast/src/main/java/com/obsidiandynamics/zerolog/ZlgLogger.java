package com.obsidiandynamics.zerolog;

import java.util.logging.*;

import com.hazelcast.logging.*;

/**
 *  Adapter from Hazelcast's {@link ILogger} façade to Zerolog.
 */
final class ZlgLogger extends AbstractLogger {
  private static final String entrypoint = AbstractLogger.class.getName();
  
  private final Zlg zlg;
  
  ZlgLogger(Zlg zlg) {
    this.zlg = zlg;
  }

  @Override
  public void log(Level level, String message) {
    zlg.level(JulMappings.mapLevel(level)).format(message).entrypoint(entrypoint).log();
  }

  @Override
  public void log(Level level, String message, Throwable thrown) {
    zlg.level(JulMappings.mapLevel(level)).format(message).threw(thrown).entrypoint(entrypoint).log();
  }

  @Override
  public void log(LogEvent logEvent) {
    final LogRecord record = logEvent.getLogRecord();
    log(record.getLevel(), record.getMessage(), record.getThrown());
  }
  
  @Override
  public Level getLevel() {
    return JulMappings.findFirst(JulMappings.getDefaultMappings(), zlg::isEnabled);
  }

  @Override
  public boolean isLoggable(Level level) {
    return zlg.isEnabled(JulMappings.mapLevel(level));
  }
}
