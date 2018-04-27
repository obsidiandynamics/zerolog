package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.slf4j.*;
import org.slf4j.spi.*;

public final class Slf4jLogTargetTest {
  private static void enableUpTo(Logger log, LogLevel.Enum level) {
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
    final LogLevel.Enum enabledLevel = LogLevel.Enum.CONF;
    enableUpTo(log, enabledLevel);
    
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    for (LogLevel.Enum level : LogLevel.Enum.values()) {
      if (level.getLevel() != LogLevel.OFF) {
        assertEquals("level=" + level, level.getLevel() >= enabledLevel.getLevel(), target.isEnabled(level.getLevel()));
      }
    }
  }
  
  @Test(expected=UnsupportedOperationException.class)
  public void testLogMappingForLevelOff() {
    Slf4jLogTarget.LogMapping.forLevel(null, LogLevel.OFF);
  }
  
  @Test
  public void testLogWithLocationWithTag() {
    final LocationAwareLogger log = mock(LocationAwareLogger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    final Exception exception = new Exception("test exception");
    target.log(LogLevel.TRACE, "tag", "format %s", 1, new Object[] {"arg"}, exception, Zlg.entrypoint);
    
    final Marker marker = MarkerFactory.getMarker("tag");
    verify(log).log(eq(marker), eq(Zlg.entrypoint), eq(LocationAwareLogger.TRACE_INT), eq("format arg"), 
                    eq(new Object[0]), eq(exception));
  }
  
  @Test
  public void testLogWithLocationWithoutTag() {
    final LocationAwareLogger log = mock(LocationAwareLogger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    final Exception exception = new Exception("test exception");
    target.log(LogLevel.TRACE, null, "format %s", 1, new Object[] {"arg"}, exception, Zlg.entrypoint);
    
    verify(log).log(isNull(), eq(Zlg.entrypoint), eq(LocationAwareLogger.TRACE_INT), eq("format arg"), 
                    eq(new Object[0]), eq(exception));
  }
  
  @Test
  public void testLogWithLocationWithBadFormat() {
    final LocationAwareLogger log = mock(LocationAwareLogger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    final Exception exception = new Exception("test exception");
    target.log(LogLevel.TRACE, null, "format %d", 1, new Object[] {3.14}, exception, Zlg.entrypoint);
    
    verify(log).log(isNull(), eq(Zlg.entrypoint), eq(LocationAwareLogger.TRACE_INT), contains("WARNING -"), 
                    eq(new Object[0]), eq(exception));
  }
  
  
  @Test
  public void testLogWithoutLocationWithMessageAndTagAndThrowable() {
    final Logger log = mock(Logger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    final Exception exception = new Exception("test exception");
    target.log(LogLevel.TRACE, "tag", "format %s", 1, new Object[] {"arg"}, exception, Zlg.entrypoint);
    
    final Marker marker = MarkerFactory.getMarker("tag");
    verify(log).trace(eq(marker), eq("format arg"), eq(exception));
  }
  
  @Test
  public void testLogWithoutLocationWithMessageAndTag() {
    final Logger log = mock(Logger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    target.log(LogLevel.TRACE, "tag", "format %s", 1, new Object[] {"arg"}, null, Zlg.entrypoint);
    
    final Marker marker = MarkerFactory.getMarker("tag");
    verify(log).trace(eq(marker), eq("format arg"));
  }
  
  @Test
  public void testLogWithoutLocationWithMessageAndThrowable() {
    final Logger log = mock(Logger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    final Exception exception = new Exception("test exception");
    target.log(LogLevel.TRACE, null, "format %s", 1, new Object[] {"arg"}, exception, Zlg.entrypoint);
    
    verify(log).trace(eq("format arg"), eq(exception));
  }
  
  @Test
  public void testLogWithoutLocationWithMessageOnly() {
    final Logger log = mock(Logger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    target.log(LogLevel.TRACE, null, "format %s", 1, new Object[] {"arg"}, null, Zlg.entrypoint);
    
    verify(log).trace(eq("format arg"));
  }

  @Test
  public void testLogWithoutLocationWithBadFormat() {
    final Logger log = mock(Logger.class);
    final Slf4jLogTarget target = new Slf4jLogTarget(log);
    target.log(LogLevel.TRACE, null, "format %d", 1, new Object[] {3.14}, null, Zlg.entrypoint);
    
    verify(log).trace(contains("WARNING -"));
  }
}
