package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.*;

public final class ZlgBuilderTest {
  @Test
  public void testForName() {
    final LogService logService = mock(LogService.class);
    Zlg.forName("testName").withConfigService(new LogConfig().withLogService(logService)).get();
    verify(logService).get(eq("testName"));
  }

  @Test
  public void testForClass() {
    final LogService logService = mock(LogService.class);
    Zlg.forClass(ZlgBuilderTest.class).withConfigService(new LogConfig().withLogService(logService)).get();
    verify(logService).get(eq(ZlgBuilderTest.class.getName()));
  }
  
  @Test
  public void testGetDefaultConfigService() {
    final ConfigService configService = ZlgBuilder.getDefaultConfigService();
    assertNotNull(configService);
    assertTrue("configService.class=" + configService.getClass(), configService instanceof PropertiesConfigService);
  }
}
