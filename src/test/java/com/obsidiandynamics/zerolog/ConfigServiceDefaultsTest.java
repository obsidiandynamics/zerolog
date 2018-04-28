package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class ConfigServiceDefaultsTest {
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(ConfigServiceDefaults.class);
  }
  
  @Test
  public void testGetDefaultConfigService() {
    final ConfigService configService = ConfigServiceDefaults.getDefaultConfigService();
    assertNotNull(configService);
    assertTrue("configService.class=" + configService.getClass(), configService instanceof PropertiesConfigService);
  }
}
