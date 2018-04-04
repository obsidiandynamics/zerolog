package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.slf4j.*;

public final class Slf4jLogTargetTest {
  private static void enableUpTo(Logger log, LogLevel level) {
    when(log.isTraceEnabled()).thenReturn(false);
    when(log.isDebugEnabled()).thenReturn(false);
    when(log.isInfoEnabled()).thenReturn(false);
    when(log.isWarnEnabled()).thenReturn(false);
    when(log.isErrorEnabled()).thenReturn(false);
    
    switch (level) {
      case TRACE:
        when(log.isTraceEnabled()).thenReturn(true);
      case DEBUG:
        when(log.isDebugEnabled()).thenReturn(true);
      case INFO:
      case CONF:
        when(log.isInfoEnabled()).thenReturn(true);
      case WARN:
        when(log.isWarnEnabled()).thenReturn(true);
      case ERROR:
        when(log.isErrorEnabled()).thenReturn(true);
      case OFF:
        break;
        
      default:
        throw new UnsupportedOperationException("Unsupported level " + level);
    }
  }
  
  @Test
  public void testIsEnabled() {
    final Logger log = mock(Logger.class);
    final LogLevel enabledLevel = LogLevel.CONF;
    enableUpTo(log, enabledLevel);
    
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    for (LogLevel level : LogLevel.values()) {
      if (level != LogLevel.OFF) {
        assertEquals("level=" + level, level.sameOrHigherThan(enabledLevel), target.isEnabled(level));
      }
    }
  }
}
