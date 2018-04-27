package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.util.logging.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.zerolog.JulMappings.*;

public final class JulMappingsTest {
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(JulMappings.class);
  }

  @Test
  public void testGetDefaultMappings() {
    assertEquals(9, JulMappings.getDefaultMappings().length);
  }
  
  @Test
  public void testMapLevel() {
    assertEquals(LogLevel.TRACE, JulMappings.mapLevel(Level.ALL));
    assertEquals(LogLevel.TRACE, JulMappings.mapLevel(Level.FINEST));
    assertEquals(LogLevel.TRACE, JulMappings.mapLevel(Level.FINER));
    assertEquals(LogLevel.DEBUG, JulMappings.mapLevel(Level.FINE));
    assertEquals(LogLevel.CONF, JulMappings.mapLevel(Level.CONFIG));
    assertEquals(LogLevel.INFO, JulMappings.mapLevel(Level.INFO));
    assertEquals(LogLevel.WARN, JulMappings.mapLevel(Level.WARNING));
    assertEquals(LogLevel.ERROR, JulMappings.mapLevel(Level.SEVERE));
    assertEquals(LogLevel.OFF, JulMappings.mapLevel(Level.OFF));
  }
  
  private static LevelMapping[] buildTestMappings() {
    final LevelMapping[] mappings = new LevelMapping[3];
    int i = 0;
    mappings[i++] = new LevelMapping(Level.INFO, LogLevel.INFO);
    mappings[i++] = new LevelMapping(Level.WARNING, LogLevel.WARN);
    mappings[i++] = new LevelMapping(Level.SEVERE, LogLevel.ERROR);
    return mappings;
  }

  @Test
  public void testFindFirst() {
    final Level first = JulMappings.findFirst(buildTestMappings(), level -> level == LogLevel.WARN);
    assertEquals(Level.WARNING, first);
  }

  @Test(expected=IllegalStateException.class)
  public void testFindFirstNonExistant() {
    JulMappings.findFirst(buildTestMappings(), level -> level == LogLevel.OFF);
  }
}
