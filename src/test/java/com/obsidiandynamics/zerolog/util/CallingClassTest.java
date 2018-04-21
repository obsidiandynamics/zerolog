package com.obsidiandynamics.zerolog.util;

import static org.junit.Assert.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class CallingClassTest {
  private final Class<?> staticField = CallingClass.forDepth(1);
  
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(CallingClass.class);
  }

  @Test
  public void testCallerDepth1() {
    final Class<?> cls = CallingClass.forDepth(1);
    assertEquals(CallingClassTest.class, cls);
  }
  
  private static class Nested {
    static Class<?> get() {
      return CallingClass.forDepth(2);
    }
  }

  @Test
  public void testCallerDepth2() {
    final Class<?> cls = Nested.get();
    assertEquals(CallingClassTest.class, cls);
  }
  
  @Test
  public void testStaticField() {
    assertEquals(CallingClassTest.class, staticField);
  }
}
