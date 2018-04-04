package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class LogConfigTest {
  @Test
  public void test() {
    final LogLevel baseLevel = LogLevel.DEBUG;
    final LogService logService = __name -> null;
    final LogConfig config = new LogConfig()
        .withBaseLevel(baseLevel)
        .withLogService(logService);
    assertEquals(baseLevel, config.getBaseLevel());
    assertEquals(logService, config.getLogService());
    Assertions.assertToStringOverride(config);
  }
  
  @Test
  public void testDefaults() {
    final LogConfig config = new LogConfig();
    assertEquals(LogConfig.getDefaultBaseLevel(), config.getBaseLevel());
    assertEquals(LogConfig.getDefaultLogService(), config.getLogService());
  }
}
