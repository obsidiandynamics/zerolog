package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class LogLevelTest {
  @Test
  public void testMatch() {
    for (LogLevel.Enum e : LogLevel.Enum.values()) {
      assertEquals(e, LogLevel.Enum.match(e.getLevel()));
    }
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFailedMatch() {
    LogLevel.Enum.match(16);
  }
  
  @Test
  public void testShortName() {
    for (LogLevel.Enum e : LogLevel.Enum.values()) {
      assertEquals(3, e.getShortName().length());
    }
  }
  
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(LogLevel.class);
  }
}
