package com.obsidiandynamics.zerolog;

import java.util.logging.*;

/**
 *  A {@link java.util.logging.Handler} wrapping a {@link Zlg} instance.
 */
public final class ZlgHandler extends Handler {
  private static final String entrypoint = Logger.class.getName();
  
  /** Used to format JUL messages. */
  private static final SimpleFormatter simpleFormatter = new SimpleFormatter();
  
  private final Zlg zlg;
  
  public ZlgHandler() {
    this(Zlg.forName("").get());
  }
  
  ZlgHandler(Zlg zlg) {
    this.zlg = zlg;
  }

  @Override
  public void publish(LogRecord record) {
    zlg
    .level(JulMappings.mapLevel(record.getLevel()))
    .format("%s")
    .arg(Args.map(Args.ref(record), simpleFormatter::formatMessage))
    .threw(record.getThrown())
    .entrypoint(entrypoint)
    .log();
  }

  @Override
  public void flush() {}

  @Override
  public void close() throws SecurityException {}
}
