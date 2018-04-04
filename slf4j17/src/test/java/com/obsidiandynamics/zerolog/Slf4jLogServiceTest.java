package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class Slf4jLogServiceTest {
  @Test
  public void test() {
    final Slf4jLogService service = new Slf4jLogService();
    final LogTarget target = service.get("name");
    assertNotNull(target);
    assertTrue("target.class=" + target.getClass(), target instanceof Slf4jLogTarget);
  }
}
