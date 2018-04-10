package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.obsidiandynamics.zerolog.Zlg.*;

public final class ZlgTest {
  @Test
  public void testDefaultMethods() {
    final Zlg z = mock(Zlg.class);
    when(z.t(any())).thenCallRealMethod();
    when(z.d(any())).thenCallRealMethod();
    when(z.c(any())).thenCallRealMethod();
    when(z.i(any())).thenCallRealMethod();
    when(z.w(any())).thenCallRealMethod();
    when(z.e(any())).thenCallRealMethod();
    
    final LogChain chain = mock(LogChain.class);
    when(chain.logb()).thenCallRealMethod();
    assertTrue(chain.logb());
    verify(chain).log();
    
    when(z.level(anyInt())).thenReturn(chain);
    
    z.t("trace");
    verify(z).level(eq(LogLevel.TRACE));
    verify(chain).format(eq("trace"));

    z.d("debug");
    verify(z).level(eq(LogLevel.DEBUG));
    verify(chain).format(eq("debug"));

    z.c("conf");
    verify(z).level(eq(LogLevel.CONF));
    verify(chain).format(eq("conf"));

    z.i("info");
    verify(z).level(eq(LogLevel.INFO));
    verify(chain).format(eq("info"));

    z.w("warn");
    verify(z).level(eq(LogLevel.WARN));
    verify(chain).format(eq("warn"));

    z.e("error");
    verify(z).level(eq(LogLevel.ERROR));
    verify(chain).format(eq("error"));

    verify(z, times(6)).level(anyInt());
    verify(chain).logb();
    verifyNoMoreInteractions(chain);
  }
}
