package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.zerolog.MockLogTarget.*;
import com.obsidiandynamics.zerolog.Zlg.*;

public final class MockLogTargetTest {
  @Test
  public void testEntryToString() {
    final MockLogTarget target = new MockLogTarget(LogLevel.TRACE);
    final Zlg zlg = target.logger();
    
    zlg.t("message");
    final List<LogEntry> entries = target.entries().list();
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
      zlg.level(logLevel).format(format).arg(i).tag(String.valueOf(i)).threw(cause).log();
    }
    
    final LogEntries logEntries = target.entries();
    assertEquals(numEntries, logEntries.count());
    logEntries.assertCount(numEntries);
    logEntries.assertCountAtLeast(numEntries);
    logEntries.assertCountAtMost(numEntries);
    
    Assertions.assertToStringOverride(logEntries);
    
    final List<LogEntry> entries = logEntries.list();
    assertEquals(numEntries, entries.size());
    
    for (int i = 0; i < numEntries; i++) {
      final LogEntry entry = entries.get(i);
      assertTrue("entry=" + entry, entry.getTimestamp() >= startTime);
      assertEquals(logLevel, entry.getLevel());
      assertEquals(String.valueOf(i), entry.getTag());
      assertEquals(format, entry.getFormat());
      assertEquals(Arrays.asList(i), entry.getArgs());
      assertEquals(cause, entry.getThrowable());
      assertEquals("entry #" + i, entry.getMessage());
      assertEquals(LogChain.entrypoint, entry.getEntrypoint());
    }
  }
  
  @Test(expected=CountAssertionError.class)
  public void testAssertCountFail() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    target.entries().assertCount(2);
  }
  
  @Test(expected=CountAssertionError.class)
  public void testAssertCountAtLeastFail() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    target.entries().assertCountAtLeast(3);
  }
  
  @Test(expected=CountAssertionError.class)
  public void testAssertCountAtMostFail() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    target.entries().assertCountAtMost(0);
  }
  
  @Test
  public void testEntriesIterator() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    
    final Iterator<LogEntry> it = target.entries().iterator();
    assertTrue(it.hasNext());
    assertNotNull(it.next());
    assertFalse(it.hasNext());
  }
  
  @Test(expected=IncompleteStateException.class)
  public void testIncompleteNotNotPermitted() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    target.entries().not().count();
  }
  
  @Test(expected=IncompleteStateException.class)
  public void testDoubleNotNotPermitted() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("message");
    target.entries().not().not();
  }
  
  @Test
  public void testForLevel() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    final List<LogEntry> entries = target.entries().forLevel(LogLevel.DEBUG).list();
    assertEquals(1, entries.size());
    assertEquals("debug", entries.get(0).getMessage());
    
    target.entries().not().forLevel(LogLevel.DEBUG).assertCount(2);
  }
  
  @Test
  public void testForLevelAndAbove() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    final List<LogEntry> entries = target.entries().forLevelAndAbove(LogLevel.DEBUG).list();
    assertEquals(2, entries.size());
    assertEquals("debug", entries.get(0).getMessage());
    assertEquals("conf", entries.get(1).getMessage());
    
    target.entries().forLevelAndAbove(LogLevel.INFO).assertCount(0);
    target.entries().not().forLevelAndAbove(LogLevel.INFO).assertCount(3);
  }
  
  @Test
  public void testForLevelAndBelow() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    final List<LogEntry> entries = target.entries().forLevelAndBelow(LogLevel.DEBUG).list();
    assertEquals(2, entries.size());
    assertEquals("trace", entries.get(0).getMessage());
    assertEquals("debug", entries.get(1).getMessage());

    target.entries().forLevelAndBelow(LogLevel.INFO).assertCount(3);
    target.entries().not().forLevelAndBelow(LogLevel.INFO).assertCount(0);
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
    
    // test after()
    assertEquals(3, target.entries().after(startTime - 1).count());
    assertEquals(0, target.entries().after(endTime).count());
    assertEquals(3, target.entries().not().after(endTime).count());
    
    // test before()
    assertEquals(3, target.entries().before(endTime + 1).count());
    assertEquals(0, target.entries().before(startTime).count());
    assertEquals(3, target.entries().not().before(startTime).count());
    
    // test 'between', as a conjunction of after() and before()
    assertEquals(3, target.entries().after(startTime - 1).before(endTime + 1).count());
    assertEquals(0, target.entries().after(endTime + 1).before(startTime - 1).count());
    
    // test that not().before() ≡ after(), as well as not().after() ≡ before()
    assertEquals(0, target.entries().not().after(startTime - 1).not().before(endTime + 1).count());
    assertEquals(3, target.entries().not().after(endTime + 1).not().before(startTime - 1).count());
  }
  
  @Test
  public void testTagged() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.tag("tag"));
    zlg.c("conf");
    
    assertEquals(1, target.entries().tagged("tag").count());
    assertEquals(0, target.entries().tagged("foo").count());
    assertEquals(2, target.entries().tagged(null).count());
    
    // 'not().tagged()' ≡ 'tagged(null)'
    assertEquals(2, target.entries().not().tagged().count());
    
    // 'not().tagged(null)' ≡ 'tagged()'
    assertEquals(1, target.entries().not().tagged(null).count());
  }
  
  @Test
  public void testTaggedAny() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace", z -> z.tag("tag"));
    zlg.d("debug", z -> z.tag("tag"));
    zlg.c("conf");
    
    assertEquals(2, target.entries().tagged().count());
  }
  
  @Test
  public void testWithThrowableType() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace", z -> z.threw(new RuntimeException("simulated")));
    zlg.d("debug", z -> z.threw(new IOException("simulated")));
    zlg.c("conf");

    assertEquals(1, target.entries().withThrowableType(RuntimeException.class).count());
    assertEquals(1, target.entries().withThrowableType(IOException.class).count());
    assertEquals(2, target.entries().withThrowableType(Throwable.class).count());
    assertEquals(1, target.entries().not().withThrowableType(Throwable.class).count());
  }
  
  @Test
  public void testWithThrowableEquals() {
    final MockLogTarget target = new MockLogTarget();
    final IOException exception = new IOException("simulated");
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.threw(exception));
    zlg.c("conf");

    assertEquals(1, target.entries().withThrowable(exception).count());
    assertEquals(2, target.entries().withThrowable(null).count());
    
    assertEquals(1, target.entries().not().withThrowable(null).count());
  }
  
  @Test
  public void testWithThrowable() {
    final MockLogTarget target = new MockLogTarget();
    final IOException exception = new IOException("simulated");
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.threw(exception));
    zlg.c("conf");
   
    assertEquals(1, target.entries().withThrowable().count());
    
    // not().withThrowable(null) ≡ withThrowable()
    assertEquals(1, target.entries().not().withThrowable(null).count());
  }
  
  @Test
  public void testWithArgEquals() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.arg("string").arg(Math.PI));
    zlg.c("conf");

    assertEquals(1, target.entries().withArg("string").count());
    assertEquals(1, target.entries().withArg(Math.PI).count());
    assertEquals(0, target.entries().withArg(Math.E).count());
    assertEquals(0, target.entries().withArg(null).count());
    
    assertEquals(0, target.entries().withArg(null).count());
  }
  
  @Test
  public void testWithArgType() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.arg("string").arg(Math.PI));
    zlg.c("conf");
    
    assertEquals(1, target.entries().withArgType(Double.class).count());
    assertEquals(0, target.entries().withArgType(Integer.class).count());

    // all three string args, as logging a message appends one string arg
    assertEquals(3, target.entries().withArgType(String.class).count());
    assertEquals(3, target.entries().withArgType(Object.class).count());
  }
  
  @Test
  public void testWithArg() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.arg("string").arg(Math.PI));
    zlg.c("conf");

    // all three have args, as logging a message appends one string arg
    assertEquals(3, target.entries().withArg().count());
  }

  @Test
  public void testWithEntrypoint() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug", z -> z.entrypoint("entrypoint"));
    zlg.c("conf");

    assertEquals(2, target.entries().withEntrypoint(Zlg.entrypoint).count());
    assertEquals(1, target.entries().withEntrypoint("entrypoint").count());

    // all log entries should have an entrypoint
    assertEquals(3, target.entries().not().withEntrypoint(null).count());
  }

  @Test
  public void testWithFormat() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace %d", z -> z.arg(3));
    zlg.d("debug %d", z -> z.arg(3));
    zlg.c("conf %d", z -> z.arg(3));
    
    assertEquals(1, target.entries().withFormat("debug %d").count());
    assertEquals(0, target.entries().withFormat("foo").count());
    assertEquals(0, target.entries().withFormat(null).count());
  }
  
  @Test
  public void testWithMessage() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace %d", z -> z.arg(3));
    zlg.d("debug %d", z -> z.arg(3));
    zlg.c("conf %d", z -> z.arg(3));
    
    assertEquals(1, target.entries().withMessage("debug 3").count());
    assertEquals(0, target.entries().withMessage("foo").count());
    assertEquals(0, target.entries().withMessage(null).count());
  }
  
  @Test
  public void testContaining() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace %d", z -> z.arg(3));
    zlg.d("debug %d", z -> z.arg(3));
    zlg.c("conf %d", z -> z.arg(3));
    
    assertEquals(1, target.entries().containing("bug 3").count());
    assertEquals(2, target.entries().not().containing("bug 3").count());
    assertEquals(3, target.entries().containing("3").count());
    assertEquals(0, target.entries().not().containing("3").count());
    assertEquals(0, target.entries().containing("foo").count());
    assertEquals(3, target.entries().containing("").count());
  }
  
  @Test
  public void testReset() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    zlg.t("trace");
    zlg.d("debug");
    zlg.c("conf");
    
    assertEquals(3, target.entries().count());
    
    target.reset();
    assertEquals(0, target.entries().count());
  }
}
