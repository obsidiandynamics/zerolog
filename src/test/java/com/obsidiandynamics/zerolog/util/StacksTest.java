package com.obsidiandynamics.zerolog.util;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class StacksTest {
  private static final class Inner {
    static final Class<?> staticField = Stacks.classForDepth(1);
  }
  
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(Stacks.class);
  }

  @Test
  public void testCallerDepth1() {
    final Class<?> cls = Stacks.classForDepth(1);
    assertEquals(StacksTest.class, cls);
  }
  
  private static class Nested {
    static Class<?> get() {
      return Stacks.classForDepth(2);
    }
  }

  @Test
  public void testCallerDepth2() {
    final Class<?> cls = Nested.get();
    assertEquals(StacksTest.class, cls);
  }
  
  @Test
  public void testStaticField() {
    assertEquals(Inner.class, Inner.staticField);
  }
  
  @Test
  public void testLocationFound() {
    final StackTraceElement element = Stacks.locate(Stacks.class.getName());
    assertNotNull(element);
    assertEquals(StacksTest.class.getName(), element.getClassName());
    assertEquals("testLocationFound", element.getMethodName());
  }
  
  @Test
  public void testLocationNotFound() {
    assertNull(Stacks.locate("foo"));
  }
  
  @Test
  public void testLocationNotFoundTooShallow() throws Throwable {
    final AtomicReference<Throwable> error = new AtomicReference<>();
    final Thread thread = new Thread(() -> {
      try {
        assertNull(Stacks.locate(Thread.class.getName()));
      } catch (Throwable e) {
        error.set(e);
      }
    });
    thread.start();
    thread.join();
    if (error.get() != null) {
      throw error.get();
    }
  }
  
  @Test
  public void testGetSimpleClassNameTopLevel() {
    final Class<?> cls = StacksTest.class;
    assertEquals(cls.getSimpleName(), Stacks.getSimpleClassName(cls.getName()));
  }
  
  @Test
  public void testGetSimpleClassNameTopLevelInvalidChars() {
    final String className = "package.$+Class";
    // the getSimpleClassName() implementation is a little more lenient than Class.getSimpleName()
    assertEquals("+Class", Stacks.getSimpleClassName(className));
  }
  
  @Test
  public void testGetSimpleClassNameInner() {
    final Class<?> cls = StacksTest.Inner.class;
    assertEquals(cls.getSimpleName(), Stacks.getSimpleClassName(cls.getName()));
  }
  
  private static class In0 {
    static class In1 {}
  }
  
  @Test
  public void testGetSimpleClassNameInner2() {
    final Class<?> cls = StacksTest.In0.In1.class;
    assertEquals(cls.getSimpleName(), Stacks.getSimpleClassName(cls.getName()));
  }
  
  @Test
  public void testGetSimpleClassNameAnonymous() {
    final Class<?> cls = new Object() {}.getClass();
    assertEquals(cls.getSimpleName(), Stacks.getSimpleClassName(cls.getName()));
  }
  
  @Test
  public void testGetSimpleClassNameLocal() {
    class $Local {}
    final Class<?> cls = $Local.class;
    // this varies from Class.getSimpleName()
    assertEquals("Local", Stacks.getSimpleClassName(cls.getName()));
  }
  
  @Test
  public void testGetSimpleClassNameDefaultPackage() {
    final Class<?> cls = int.class;
    assertEquals(cls.getSimpleName(), Stacks.getSimpleClassName(cls.getName()));
  }
  
  @Test
  public void testFormatLocationNonNull() {
    final StackTraceElement element = 
        new StackTraceElement("pkg.Foo$Bar", "doSomething", null, 42);
    assertEquals("Bar.doSomething:42", Stacks.formatLocation(element));
  }
  
  @Test
  public void testFormatLocationNull() {
    assertEquals("?.?:?", Stacks.formatLocation(null));
  }
}
