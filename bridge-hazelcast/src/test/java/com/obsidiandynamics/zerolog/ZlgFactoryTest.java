package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class ZlgFactoryTest {
  @Test
  public void testGet() {
    assertNotNull(new ZlgFactory().getLogger("name"));
  }
}
