package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.slf4j.*;

import com.obsidiandynamics.assertion.*;

public final class Slf4jWrapperTest {
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(Slf4jWrapper.class);
  }

  @Test
  public void testGet() {
    final Logger logger = mock(Logger.class);
    final LogService logService = Slf4jWrapper.of(logger);
    assertNotNull(logService);
    
    final LogTarget logTarget = logService.get("name");
    assertNotNull(logTarget);
    
    logTarget.isEnabled(LogLevel.INFO);
    verify(logger).isInfoEnabled();
  }
}
