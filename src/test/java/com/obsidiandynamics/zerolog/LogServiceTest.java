package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class LogServiceTest {
  @Test
  public void testNopCoverage() {
    final LogService logService = LogService.nop();
    assertNotNull(logService);
    assertSame(logService, LogService.nop());
    assertEquals(LogTarget.nop(), logService.get("any"));
  }
}
