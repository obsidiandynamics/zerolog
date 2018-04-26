package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.function.*;

import org.junit.*;

import com.obsidiandynamics.func.*;
import com.obsidiandynamics.zerolog.Zlg.*;

public final class NopLogChainTest {
  @Test
  public void testFluentChain() {
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
        .threw(null)
        .entrypoint("entrypoint");
    assertSame(chain, end);
    
    end.log(); // does nothing
  }
  
  @Test
  public void testLazy() {
    final NopLogChain chain = NopLogChain.getInstance();
    final BooleanSupplier booleanSupplier = mock(BooleanSupplier.class);
    final DoubleSupplier doubleSupplier = mock(DoubleSupplier.class);
    final IntSupplier intSupplier = mock(IntSupplier.class);
    final LongSupplier longSupplier = mock(LongSupplier.class);
    final Supplier<String> stringSupplier = Classes.cast(mock(Supplier.class));
    
    final LogChain end = chain
        .format("format")
        .arg(booleanSupplier)
        .arg(doubleSupplier)
        .arg(intSupplier)
        .arg(longSupplier)
        .arg(stringSupplier);
    assertSame(chain, end);
    
    end.log(); // does nothing
    
    verifyNoMoreInteractions(booleanSupplier);
    verifyNoMoreInteractions(doubleSupplier);
    verifyNoMoreInteractions(intSupplier);
    verifyNoMoreInteractions(longSupplier);
    verifyNoMoreInteractions(stringSupplier);
  }
}
