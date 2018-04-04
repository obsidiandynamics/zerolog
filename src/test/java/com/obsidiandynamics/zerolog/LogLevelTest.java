package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class LogLevelTest {
  @Test
  public void testComparison() {
    assertTrue(LogLevel.CONF.sameOrHigherThan(LogLevel.CONF));
    assertTrue(LogLevel.CONF.sameOrHigherThan(LogLevel.DEBUG));
  }
  
  @Test
  public void testShortName() {
    for (LogLevel level : LogLevel.values()) {
      assertEquals(3, level.getShortName().length());
    }
  }
}
