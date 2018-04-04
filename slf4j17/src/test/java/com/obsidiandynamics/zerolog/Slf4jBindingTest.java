package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class Slf4jBindingTest {
  @Test
  public void test() {
    final Slf4jBinding binding = new Slf4jBinding();
    assertTrue("priority=" + binding.getPriority(), binding.getPriority() > new SysOutBinding().getPriority());
    assertNotNull(binding.getLogService());
    assertEquals(Slf4jLogService.class, binding.getLogService().getClass());
  }
}
