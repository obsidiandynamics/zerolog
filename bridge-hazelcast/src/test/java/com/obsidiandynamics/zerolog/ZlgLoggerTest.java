package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.logging.*;

import org.junit.*;
import org.mockito.*;

import com.hazelcast.instance.*;
import com.hazelcast.logging.*;

public final class ZlgLoggerTest {
  @Test
  public void testLogEvent() {
    final MockLogTarget logTarget = new MockLogTarget();
    final ZlgLogger logger = new ZlgLogger(logTarget.logger());
    final IOException cause = new IOException("test exception");
    final LogRecord record = new LogRecord(Level.WARNING, "test message");
    record.setThrown(cause);
    final LogEvent event = new LogEvent(record, new SimpleMemberImpl());
    
    logger.log(event);
    logTarget.entries().assertCount(1);
    logTarget.entries()
    .forLevel(LogLevel.WARN).containing("test message").withThrowableType(IOException.class).assertCount(1);
  }
  
  @Test
  public void testGetLevel() {
    final Zlg zlg = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    when(zlg.isEnabled(anyInt())).thenAnswer(invocation -> {
      final int level = invocation.getArgument(0);
      return level >= LogLevel.WARN;
    });
    final ZlgLogger logger = new ZlgLogger(zlg);
    
    assertEquals(Level.WARNING, logger.getLevel());
  }
  
  @Test
  public void testIsLoggable() {
    final Zlg zlg = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    when(zlg.isEnabled(anyInt())).thenAnswer(invocation -> {
      final int level = invocation.getArgument(0);
      return level >= LogLevel.WARN;
    });
    final ZlgLogger logger = new ZlgLogger(zlg);
    
    assertFalse(logger.isLoggable(Level.INFO));
    assertTrue(logger.isLoggable(Level.WARNING));
    assertTrue(logger.isLoggable(Level.SEVERE));
  }
}
