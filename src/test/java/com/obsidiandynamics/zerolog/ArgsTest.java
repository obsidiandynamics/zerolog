package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.func.*;

public final class ArgsTest {
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(Args.class);
  }

  @Test
  public void testRef() {
    final Object obj = "obj";
    assertSame(obj, Args.ref(obj).get());
  }
  
  @Test
  public void testMap() {
    final Supplier<String> supplier = Classes.cast(mock(Supplier.class));
    when(supplier.get()).thenReturn("str");
    
    final Function<String, Object> transform = Classes.cast(mock(Function.class));
    when(transform.apply(any())).thenReturn("transformed");
    
    final Supplier<?> x = Args.map(supplier, transform);
    verifyNoMoreInteractions(supplier);
    verifyNoMoreInteractions(transform);
    
    assertEquals("transformed", x.get());
    verify(supplier).get();
    verify(transform).apply(eq("str"));
  }
}
