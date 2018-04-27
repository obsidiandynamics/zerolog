package com.obsidiandynamics.zerolog;

import java.util.logging.*;

import org.junit.*;

public final class ZlgHandlerTest {
  @Test
  public void testFlushAndCloseCoverage() {
    final ZlgHandler handler = new ZlgHandler(Zlg.nop());
    handler.flush();
    handler.close();
  }

  @Test
  public void testPublish() {
    final MockLogTarget target = new MockLogTarget();
    final ZlgHandler handler = new ZlgHandler(target.logger());
    
    final LogRecord record = new LogRecord(Level.FINE, "Pi is {0}");
    record.setParameters(new Object[] {3.14});
    
    handler.publish(record);
    target.entries().assertCount(1);
    target.entries().forLevel(LogLevel.DEBUG).withMessage("Pi is 3.14").assertCount(1);
  }
  
  @Test
  public void testDefaultConstructor() {
    final ZlgHandler handler = new ZlgHandler();
    handler.close();
  }
}
