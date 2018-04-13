package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.*;

import org.junit.*;
import org.mockito.*;

import com.obsidiandynamics.func.*;
import com.obsidiandynamics.zerolog.Zlg.*;

public final class ZlgTest {
  @Test
  public void testDefaultMethods() {
    final Zlg z = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    assertTrue(chain.log());
    verify(chain).done();
    
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
    verify(chain).log();
    verifyNoMoreInteractions(chain);
  }
  
  @Test
  public void testChainWithLambda() {
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    final Consumer<LogChain> logChainConsumer = Classes.cast(mock(Consumer.class));
    
    chain.with(logChainConsumer);
    verify(logChainConsumer).accept(eq(chain));
    verify(chain).done();
  }
  
  @Test
  public void testZlgWithLambda() {
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(chain.format(any())).thenReturn(chain);
    final Zlg z = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    when(z.level(anyInt())).thenReturn(chain);
    
    final Consumer<LogChain> logChainConsumer = Classes.cast(mock(Consumer.class));
    
    z.t("format", logChainConsumer);
    verify(z).level(eq(LogLevel.TRACE));
    
    z.d("format", logChainConsumer);
    verify(z).level(eq(LogLevel.DEBUG));

    z.c("format", logChainConsumer);
    verify(z).level(eq(LogLevel.CONF));

    z.i("format", logChainConsumer);
    verify(z).level(eq(LogLevel.INFO));

    z.w("format", logChainConsumer);
    verify(z).level(eq(LogLevel.WARN));

    z.e("format", logChainConsumer);
    verify(z).level(eq(LogLevel.ERROR));
    
    verify(logChainConsumer, times(6)).accept(eq(chain));
    verify(chain, times(6)).done();
  }
}
