package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class SysOutBindingTest {
  @Test
  public void test() {
    final SysOutBinding binding = new SysOutBinding();
    assertEquals(0, binding.getPriority());
    assertNotNull(binding.getLogService());
    assertEquals(SysOutLogService.class, binding.getLogService().getClass());
  }
}
