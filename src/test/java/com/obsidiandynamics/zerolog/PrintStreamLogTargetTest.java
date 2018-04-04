package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

public class PrintStreamLogTargetTest extends AbstractLogTargetTest {
  @Test
  public void testIsEnabled() {
    assertTrue(new PrintStreamLogTarget(null).isEnabled(LogLevel.TRACE));
  }
  
  @Test
  public void testLogWithTag() {
    final StringStream ss = new StringStream();
    final PrintStreamLogTarget target = new PrintStreamLogTarget(new PrintStream(ss));
    final String threadName = Thread.currentThread().getName();
    final String tag = "abracadabra";
    target.log(LogLevel.TRACE, tag, "message %d %d %d", 3, new Object[]{100, 200, 300, 400}, null);
    final String out = ss.getString();
    
    assertHas(out, threadName);
    assertHas(out, tag);
    assertHas(out, "message");
    assertHas(out, "100");
    assertHas(out, "200");
    assertHas(out, "300");
    assertHasNot(out, "400");
  }
  
  @Test
  public void testLogWithoutTag() {
    final StringStream ss = new StringStream();
    final PrintStreamLogTarget target = new PrintStreamLogTarget(new PrintStream(ss));
    final String threadName = Thread.currentThread().getName();
    target.log(LogLevel.TRACE, null, "message %d %d %d", 3, new Object[]{100, 200, 300, 400}, null);
    final String out = ss.getString();
    
    assertHas(out, threadName);
    assertHas(out, "message");
    assertHas(out, "100");
    assertHas(out, "200");
    assertHas(out, "300");
    assertHasNot(out, "400");
  }
  
  @Test
  public void testLogWithTagAndException() {
    final StringStream ss = new StringStream();
    final PrintStreamLogTarget target = new PrintStreamLogTarget(new PrintStream(ss));
    final String threadName = Thread.currentThread().getName();
    final String tag = "abracadabra";
    final Exception exception = new Exception("simulated error");
    target.log(LogLevel.TRACE, tag, "message %d %d %d", 3, new Object[]{100, 200, 300, 400}, exception);
    final String out = ss.getString();
    
    assertHas(out, threadName);
    assertHas(out, tag);
    assertHas(out, "message");
    assertHas(out, "100");
    assertHas(out, "200");
    assertHas(out, "300");
    assertHasNot(out, "400");
    assertHas(out, exception.getMessage());
    assertHas(out, "at");
    assertHas(out, "testLogWithTagAndException");
  }
}
