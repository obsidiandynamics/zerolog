package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.io.*;

public abstract class AbstractLogTargetTest {
  protected static final class StringStream extends ByteArrayOutputStream {
    String getString() {
      return new String(toByteArray());
    }
  }
  
  protected static final void assertHas(String str, String substr) {
    assertTrue("str=" + str, str.contains(substr));
  }
  
  protected static final void assertHasNot(String str, String substr) {
    assertFalse("str=" + str, str.contains(substr));
  }
}
