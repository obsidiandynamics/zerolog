package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.zerolog.Zlg.*;

public final class NopLogChainTest {
  @Test
  public void test() {
    final NopLogChain chain = NopLogChain.getInstance();
    final LogChain end = chain
        .tag("tag")
        .format("format")
        .arg(true)
        .arg((byte) 0x01)
        .arg('c')
        .arg(3.14d)
        .arg(3.14f)
        .arg(42)
        .arg(42L)
        .arg("string")
        .arg((short) 42)
        .threw(null);
    assertSame(chain, end);
  }
}
