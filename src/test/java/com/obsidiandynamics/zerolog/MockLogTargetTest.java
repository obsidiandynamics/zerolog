package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.zerolog.MockLogTarget.*;

public final class MockLogTargetTest {
  @Test
  public void testEntryToString() {
    final MockLogTarget target = new MockLogTarget(LogLevel.TRACE);
    final Zlg zlg = target.logger();
    
    zlg.t("message");
    final List<Entry> entries = target.entries().list();
    assertNotNull(entries);
    assertEquals(1, entries.size());
    Assertions.assertToStringOverride(entries.iterator().next());
  }
  
  @Test
  public void testIsEnabled() {
    final MockLogTarget target = new MockLogTarget(LogLevel.INFO);
    final Zlg zlg = Zlg.forName("mock")
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE).withLogService(target.logService()))
        .get();
    assertFalse(zlg.isEnabled(LogLevel.CONF));
    assertTrue(zlg.isEnabled(LogLevel.INFO));
    assertTrue(zlg.isEnabled(LogLevel.WARN));
  }
  
  @Test
  public void testEntryRetention() {
    final int numEntries = 10;
    final int logLevel = LogLevel.TRACE;
    final MockLogTarget target = new MockLogTarget(logLevel);
    final Zlg zlg = target.logger();
    
    final long startTime = System.currentTimeMillis();
    final String format = "entry #%d";
    final Exception cause = new Exception("simulated");
    for (int i = 0; i < numEntries; i++) {
      zlg.level(logLevel).format(format).arg(i).tag(String.valueOf(i)).threw(cause).done();
    }
    
    final List<Entry> entries = target.entries().list();
    assertEquals(numEntries, entries.size());
    
    for (int i = 0; i < numEntries; i++) {
      final Entry entry = entries.get(i);
      assertTrue("entry=" + entry, entry.getTimestamp() >= startTime);
      assertEquals(logLevel, entry.getLevel());
      assertEquals(String.valueOf(i), entry.getTag());
      assertEquals(format, entry.getFormat());
      assertArrayEquals(new Object[] {i}, entry.getArgs());
      assertEquals(cause, entry.getThrowable());
      assertEquals("entry #" + i, entry.getMessage());
    }
  }
  
  @Test
  public void testEntriesIterator() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    
    final Iterator<Entry> it = target.entries().iterator();
    assertTrue(it.hasNext());
    assertNotNull(it.next());
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testForLevel() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    final List<Entry> entries = target.entries().forLevel(LogLevel.DEBUG).list();
    assertEquals(1, entries.size());
    assertEquals("debug", entries.get(0).getMessage());
  }
  
  @Test
  public void testForLevelAndAbove() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    final List<Entry> entries = target.entries().forLevelAndAbove(LogLevel.DEBUG).list();
    assertEquals(2, entries.size());
    assertEquals("debug", entries.get(0).getMessage());
    assertEquals("conf", entries.get(1).getMessage());
  }
  
  @Test
  public void testBeforeAfter() {
    final long startTime = System.currentTimeMillis();
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    final long endTime = System.currentTimeMillis();
    
    assertEquals(3, target.entries().after(startTime - 1).list().size());
    assertEquals(0, target.entries().after(endTime).list().size());
    
    assertEquals(3, target.entries().before(endTime + 1).list().size());
    assertEquals(0, target.entries().before(startTime).list().size());
  }
  
  @Test
  public void testTag() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.tag("tag"));
    zlg.c("conf");
    
    assertEquals(1, target.entries().tagged("tag").list().size());
  }
  
  @Test
  public void testWithException() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.threw(new IOException("simulated")));
    zlg.c("conf");

    assertEquals(0, target.entries().withException(RuntimeException.class).list().size());
    assertEquals(1, target.entries().withException(IOException.class).list().size());
  }
  
  @Test
  public void testContaining() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    assertEquals(1, target.entries().containing("bug").list().size());
  }
  
  @Test
  public void testReset() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    assertEquals(3, target.entries().list().size());
    
    target.reset();
    assertEquals(0, target.entries().list().size());
  }
}
