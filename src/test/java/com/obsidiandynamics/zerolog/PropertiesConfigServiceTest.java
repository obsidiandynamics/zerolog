package com.obsidiandynamics.zerolog;

import static com.obsidiandynamics.zerolog.PropertiesConfigService.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.junit.*;

public final class PropertiesConfigServiceTest {
  public static class NopLogService implements LogService {
    @Override
    public LogTarget get(String name) {
      throw new UnsupportedOperationException();
    }
  }
  
  @Test
  public void testLoadConfigDefaultsAndCache() {
    final LogConfig defaultConfig = new LogConfig();
    final int defaultBaseLevel = defaultConfig.getBaseLevel();
    final LogService defaultLogService = defaultConfig.getLogService();
    
    final PropertiesConfigService configService = new PropertiesConfigService(Properties::new);
    final LogConfig config = configService.get();
    assertNotNull(config);
    assertEquals(defaultBaseLevel, config.getBaseLevel());
    assertSame(defaultLogService, config.getLogService());
    
    final LogConfig config2 = configService.get();
    assertSame(config, config2);
  }
  
  @Test
  public void testLoadConfigSuccessAndCache() {
    final Properties props = new Properties();
    props.setProperty(KEY_BASE_LEVEL, LogLevel.Enum.WARN.name());
    props.setProperty(KEY_LOG_SERVICE, NopLogService.class.getName());
    
    final PropertiesConfigService configService = new PropertiesConfigService(() -> props);
    final LogConfig config = configService.get();
    assertNotNull(config);
    assertEquals(LogLevel.WARN, config.getBaseLevel());
    assertNotNull(config.getLogService());
    assertEquals(NopLogService.class, config.getLogService().getClass());
    
    final LogConfig config2 = configService.get();
    assertSame(config, config2);
  }
  
  @Test(expected=PropertiesLoadException.class)
  public void testLoadFailure() {
    new PropertiesConfigService(() -> { throw new Exception("simulated error"); }).get();
  }
  
  @Test(expected=FileNotFoundException.class)
  public void testFileLoadFailure() throws Exception {
    PropertiesConfigService.forUri(URI.create("file://nonexistentfile")).get();
  }
  
  @Test(expected=FileNotFoundException.class)
  public void testClasspathLoadFailure() throws Exception {
    PropertiesConfigService.forUri(URI.create("cp://nonexistentfile")).get();
  }
  
  @Test
  public void testClasspathLoadFailureWithFailsafe() throws Exception {
    PropertiesConfigService.forUri(URI.create("cp://nonexistentfile"), URI.create("cp://testconfig.properties")).get();
  }
  
  @Test(expected=ServiceInstantiationException.class)
  public void testInstantiationFailure() {
    final Properties props = new Properties();
    props.setProperty(KEY_LOG_SERVICE, "nonexistentpackage.NonExistentClass");
    new PropertiesConfigService(() -> props).get();
  }
  
  @Test
  public void testFromFile() {
    final LogConfig config = new PropertiesConfigService(forUri(URI.create("cp://testconfig.properties"))).get();
    assertNotNull(config);
    assertEquals(LogLevel.WARN, config.getBaseLevel());
    assertNotNull(config.getLogService());
    assertEquals(SysOutLogService.class, config.getLogService().getClass());
  }
}
