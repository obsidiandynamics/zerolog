package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class LogConfigTest {
  @Test
  public void test() {
    final LogLevel rootLevel = LogLevel.DEBUG;
    final LogService logService = __name -> null;
    final LogConfig config = new LogConfig()
        .withRootLevel(rootLevel)
        .withLogService(logService);
    assertEquals(rootLevel, config.getRootLevel());
    assertEquals(logService, config.getLogService());
    Assertions.assertToStringOverride(config);
  }
  
  @Test
  public void testDefaults() {
    final LogConfig config = new LogConfig();
    assertEquals(LogConfig.getDefaultLevel(), config.getRootLevel());
    assertEquals(LogConfig.getDefaultLogService(), config.getLogService());
  }
}
