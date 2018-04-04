package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class SysOutLogServiceTest {
  @Test
  public void test() {
    final SysOutLogService service = new SysOutLogService();
    final LogTarget target = service.get("name");
    assertNotNull(target);
    assertTrue("target.class=" + target.getClass(), target instanceof PrintStreamLogTarget);
  }
}
