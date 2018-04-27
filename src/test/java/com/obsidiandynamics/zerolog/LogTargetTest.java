package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class LogTargetTest {
  @Test
  public void testNopCoverage() {
    final LogTarget logTarget = LogTarget.nop();
    assertNotNull(logTarget);
    assertSame(logTarget, LogTarget.nop());
    for (LogLevel.Enum level : LogLevel.Enum.values()) {
      assertFalse(logTarget.isEnabled(level.getLevel()));
      logTarget.log(level.getLevel(), "tag", "format", 0, new Object[0], null, "entrypoint");
    }
  }
}
